package com.ussd.dto.request;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TruRouteRequest {
    @JacksonXmlProperty(localName = "msg")
    private String msg;
    @JacksonXmlProperty(localName = "msisdn")
    private String msisdn;
    @JacksonXmlProperty(localName = "sessionid")
    private String sessionid;
    @JacksonXmlProperty(localName = "type")
    private String type;
}
