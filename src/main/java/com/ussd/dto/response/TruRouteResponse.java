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
public class TruRouteResponse {
    @JacksonXmlProperty(localName = "type")
    private String type;
    @JacksonXmlProperty(localName = "msg")
    private String msg;
    @JacksonXmlProperty(localName = "premium")
    private Premium premium;
}
