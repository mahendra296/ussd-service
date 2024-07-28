package com.ussd.dto.response;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessUssdPageResponse {
    @JacksonXmlProperty(localName = "msisdn")
    private String msisdn;
    @JacksonXmlProperty(localName = "message")
    private String message;
    @JacksonXmlProperty(localName = "shouldEndSession")
    private Boolean shouldEndSession;
}
