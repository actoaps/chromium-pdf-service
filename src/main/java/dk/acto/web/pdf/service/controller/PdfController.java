package dk.acto.web.pdf.service.controller;

import com.github.kklisura.cdt.launch.ChromeArguments;
import com.github.kklisura.cdt.launch.ChromeLauncher;
import dk.acto.web.pdf.dto.ActoConf;
import dk.acto.web.pdf.dto.RequestDescription;
import dk.acto.web.pdf.exception.InvalidPDFPage;
import dk.acto.web.pdf.exception.MethodNotEnabled;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.concurrent.CountDownLatch;


@AllArgsConstructor
@RestController
@RequestMapping("/")
@Slf4j
@PreAuthorize("isAuthenticated()")
public class PdfController {
    private final ActoConf actoConf;

    @GetMapping(value = "{url}/{file}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> getFile(@PathVariable final String url, @PathVariable final String file) {
        if (!actoConf.getEnabledGet()) {
            throw new MethodNotEnabled();
        }

        return generatePDF(RequestDescription.builder()
                .file(file)
                .url(url)
                .build());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> postFile(@RequestBody final RequestDescription requestDescription) {
        if (!actoConf.getEnabledPost()) {
            throw new MethodNotEnabled();
        }

        return generatePDF(requestDescription);
    }

    private ResponseEntity<Resource> generatePDF(final RequestDescription requestDescription) {
        final var chromeArguments = ChromeArguments.builder()
                .noFirstRun()
                .disableGpu()
                .headless()
                .disableBackgroundNetworking()
                .disableDefaultApps()
                .disableExtensions()
                .disableSync()
                .disableTranslate()
                .hideScrollbars()
                .metricsRecordingOnly()
                .disablePromptOnRepost()
                .disableHangMonitor()
                .disableClientSidePhishingDetection()
                .disableBackgroundTimerThrottling()
                .muteAudio()
                .safebrowsingDisableAutoUpdate()
                .additionalArguments("virtual-time-budget", "10000")
                .additionalArguments("run-all-compositor-stages-before-draw", true)
                .additionalArguments("no-sandbox", true)
                .additionalArguments("ignore-certificate-errors", true)
                .additionalArguments("ignore-ssl-errors", true)
                .additionalArguments("ignore-certificate-errors-spki-list", true)
                .additionalArguments("disable-dev-shm-usage", true)
                .additionalArguments("enable-automation", true)
                .additionalArguments("disable-features", "site-per-process,TranslateUI")
                .additionalArguments("enable-features", "NetworkService,NetworkServiceInProcess")
                .build();

        final var launcher = new ChromeLauncher();
        final var chromeService = launcher.launch(chromeArguments);

        final var tab = chromeService.createTab();

        final var devToolsService = chromeService.createDevToolsService(tab);
        final var latch = new CountDownLatch(1);
        final var page = devToolsService.getPage();
        page.setLifecycleEventsEnabled(true);
        page.onLoadEventFired(x -> latch.countDown());

        page.enable();

        return Try.of(() -> page.navigate(requestDescription.getUrl()))
                .andThenTry(latch::await)
                .map(x -> page.printToPDF())
                .map(x -> Base64.getDecoder().decode(x.getData()))
                .map(ByteArrayResource::new)
                .map(x -> ResponseEntity.ok((Resource) x))
                .andFinally(() -> {
                    chromeService.closeTab(tab);
                    launcher.close();
                    devToolsService.close();
                })
                .onFailure(x -> log.error(String.format("Caught Exception for %s / %s", requestDescription.getUrl(), requestDescription.getFile()), x))
                .getOrElseThrow(InvalidPDFPage::new);
    }
}
