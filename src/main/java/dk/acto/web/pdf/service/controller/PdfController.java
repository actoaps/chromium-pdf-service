package dk.acto.web.pdf.service.controller;

import dk.acto.web.pdf.dto.ActoConf;
import dk.acto.web.pdf.dto.RequestDescription;
import dk.acto.web.pdf.exception.MethodNotEnabled;
import dk.acto.web.pdf.service.PdfService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@AllArgsConstructor
@RestController
@RequestMapping("/")
@PreAuthorize("isAuthenticated()")
public class PdfController {
    private final ActoConf actoConf;
    private final PdfService pdfService;

    @GetMapping(value = "{url}/{file}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> getFile(@PathVariable final String url, @PathVariable final String file) {
        if (!actoConf.getEnabledGet()) {
            throw new MethodNotEnabled();
        }

        return pdfService.generatePdf(url, file);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> postFile(@RequestBody final RequestDescription requestDescription) {
        if (!actoConf.getEnabledPost()) {
            throw new MethodNotEnabled();
        }

        return pdfService.generatePdf(requestDescription.getUrl(), requestDescription.getFile());
    }
}
