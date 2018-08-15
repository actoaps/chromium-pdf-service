package dk.acto.web.pdf.config;

import dk.acto.web.pdf.rest.PdfController;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

@Component
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        register(PdfController.class);
    }

}