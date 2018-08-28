package dk.acto.web.pdf.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(builder = ActoConf.ActoConfBuilder.class)
public class ActoConf {
    public static final ActoConf DEFAULT = ActoConf.builder()
            .enabledGet(true)
            .enabledPost(true)
            .secret("secret")
            .build();

    private final boolean enabledGet;
    private final boolean enabledPost;
    private final String secret;

    @JsonPOJOBuilder(withPrefix = "")
    public static final class ActoConfBuilder {
    }

}
