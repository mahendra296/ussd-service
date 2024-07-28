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
public class Premium {
    @JacksonXmlProperty(localName = "cost")
    private String cost;
    @JacksonXmlProperty(localName = "ref")
    private String ref;
}
