package dk.acto.web.pdf.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RequestDescription {
    String url;
    String file;
}
