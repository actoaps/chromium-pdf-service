package dk.acto.web.pdf.service;

import com.github.kklisura.cdt.launch.ChromeArguments;
import com.github.kklisura.cdt.launch.ChromeLauncher;
import dk.acto.web.pdf.exception.InvalidPDFPage;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.concurrent.CountDownLatch;

@Slf4j
@Service
@AllArgsConstructor
public class PdfService {
    public ResponseEntity<Resource> generatePdf(final String url, final String filename) {
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
        page.onLoadEventFired(x -> latch.countDown());

        page.enable();

        return Try.of(() -> page.navigate(url))
                .andThenTry(latch::await)
                .map(x -> page.printToPDF())
                .map(x -> Base64.getDecoder().decode(x.getData()))
                .map(ByteArrayResource::new)
                .map(x -> ResponseEntity.ok().
                        header("Content-Disposition", String.format("attachment; filename=\"%s\"", filename)).
                        body((Resource) x))
                .andFinally(() -> {
                    chromeService.closeTab(tab);
                    launcher.close();
                    devToolsService.close();
                })
                .onFailure(x -> log.error(String.format("Caught Exception for %s / %s", url, filename), x))
                .getOrElseThrow(InvalidPDFPage::new);
    }
}
