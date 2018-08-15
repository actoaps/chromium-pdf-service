package dk.acto.web.pdf.rest;

import com.github.kklisura.cdt.launch.ChromeArguments;
import com.github.kklisura.cdt.launch.ChromeLauncher;
import com.github.kklisura.cdt.protocol.commands.Page;
import com.github.kklisura.cdt.services.ChromeDevToolsService;
import com.github.kklisura.cdt.services.ChromeService;
import com.github.kklisura.cdt.services.types.ChromeTab;
import dk.acto.web.pdf.dto.ActoConf;
import dk.acto.web.pdf.dto.RequestDescription;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Base64;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component
@Path("/")
@Slf4j
public class PdfController {
    private static final Pattern auth = Pattern.compile("^([Bb]earer\\s+)?(.+)$");

    private final ActoConf actoConf;

    @Autowired
    public PdfController(ActoConf actoConf) {
        this.actoConf = actoConf;
    }


    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Path("{url}/{file}")
    public Response getFile(@HeaderParam("Authorization") String authParam, @PathParam("url") final String url, @PathParam("file") final String file) {
        if (!actoConf.isEnabledGet()) {
            return Response.status(Response.Status.METHOD_NOT_ALLOWED).build();
        }

        if (!validateAuth(authParam)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        RequestDescription requestDescription = RequestDescription.builder()
                .file(file)
                .url(url)
                .build();
        return generatePDF(requestDescription);
    }

    @POST
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postFile(@HeaderParam("Authorization") String authParam, final RequestDescription requestDescription) {

        if (!actoConf.isEnabledPost()) {
            return Response.status(Response.Status.METHOD_NOT_ALLOWED).build();
        }

        if (!validateAuth(authParam)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        return generatePDF(requestDescription);
    }

    private Response generatePDF(final RequestDescription requestDescription) {

        final ChromeLauncher launcher = new ChromeLauncher();

        final ChromeService chromeService = launcher.launch(ChromeArguments.builder()
                .noFirstRun(true)
                .disableGpu(true)
                .headless(true)
                .additionalArguments("no-sandbox", true)
                .build());

        final ChromeTab tab = chromeService.createTab();

        final ChromeDevToolsService devToolsService = chromeService.createDevToolsService(tab);
        final Page page = devToolsService.getPage();

        final BlockingQueue<String> queue = new ArrayBlockingQueue<>(1);

        page.onFrameStoppedLoading(
                event -> queue.add(page.printToPDF())
        );

        page.enable();

        return Try.of(() -> page.navigate(requestDescription.getUrl()))
                .mapTry(x -> queue.take())
                .andFinally(() -> {
                    chromeService.closeTab(tab);
                    launcher.close();
                    devToolsService.waitUntilClosed();
                })
                .map(x -> Response
                        .ok(Base64.getDecoder().decode(x), MediaType.APPLICATION_OCTET_STREAM)
                        .header("content-disposition", String.format("attachment; filename = %s", requestDescription.getFile()))
                        .build())
                .onFailure(x -> log.error(String.format("Caught Exception for %s / %s", requestDescription.getUrl(), requestDescription.getFile()), x))
                .getOrElseGet(
                        x -> Response.status(Response.Status.BAD_REQUEST).build()
                );
    }

    private boolean validateAuth(String authString) {
        return Try.of(() -> auth.matcher(authString))
                .filter(Matcher::matches)
                .map(x -> x.group(2))
                .filter(x -> x.equals(actoConf.getSecret()))
                .toOption()
                .isDefined();
    }
}
