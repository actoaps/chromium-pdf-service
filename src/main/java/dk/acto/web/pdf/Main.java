package dk.acto.web.pdf;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.acto.web.pdf.dto.ActoConf;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableAutoConfiguration
@Slf4j
public class Main {
    public static void main(String[] args) {
        System.setProperty("org.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH", "true");
        SpringApplication.run(Main.class, args);
    }

    @Bean
    public static ActoConf fromEnv() {
        final ObjectMapper om = new ObjectMapper();
        String conf = System.getenv().get("ACTO_CONF");
        return Try.of( () -> om.readValue(conf, ActoConf.class))
                .onFailure(x -> log.warn(
                        String.format("Missing ACTO_CONF environment variable. Using default: %s", Try.of(() -> om.writeValueAsString(ActoConf.DEFAULT)).get())
                ))
                .getOrElse(ActoConf.DEFAULT);
    }
}
