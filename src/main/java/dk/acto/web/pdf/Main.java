package dk.acto.web.pdf;

import dk.acto.web.pdf.dto.ActoConf;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        System.setProperty("org.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH", "true");
        SpringApplication.run(Main.class, args);
    }

    @Bean
    public ActoConf actoConf(@Value("${ENABLED_GET:true}") final Boolean enabledGet,
                             @Value("${ENABLED_POST:true}") final Boolean enabledPost,
                             @Value("${SECRET:secret}") final String secret) {
        return ActoConf.builder()
                .enabledGet(enabledGet)
                .enabledPost(enabledPost)
                .secret(secret)
                .build();
    }
}
