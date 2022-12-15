package dk.acto.web.pdf;

import com.github.kklisura.cdt.services.factory.impl.DefaultWebSocketContainerFactory;
import dk.acto.web.pdf.dto.ActoConf;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@Slf4j
@SpringBootApplication
public class Main {
    public static void main(String[] args) {
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

    @Bean
    @ConditionalOnProperty(name = "PDF_BUFFER_SIZE_MB")
    public CommandLineRunner setPdfBufferSize(@Value("${PDF_BUFFER_SIZE_MB}") Integer bufferSize) {
        log.info("Setting PDF buffer size to {} MB.", bufferSize);
        return x -> System.setProperty(
                DefaultWebSocketContainerFactory.WEBSOCKET_INCOMING_BUFFER_PROPERTY,
                Integer.toString(DefaultWebSocketContainerFactory.MB * bufferSize)
        );
    }
}
