package dk.acto.web.pdf.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ActoConf {
    Boolean enabledGet;
    Boolean enabledPost;
    String secret;
}
