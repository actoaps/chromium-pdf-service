package dk.acto.web.pdf.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestDescription {
    private final String url;
    private final String file;
}
