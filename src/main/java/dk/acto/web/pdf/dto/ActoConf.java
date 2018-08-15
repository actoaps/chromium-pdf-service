package dk.acto.web.pdf.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ActoConf {
    public static final ActoConf DEFAULT = ActoConf.builder()
            .enabledGet(true)
            .enabledPost(true)
            .secret("secret")
            .build();

    private final boolean enabledGet;
    private final boolean enabledPost;
    private final String secret;
}
