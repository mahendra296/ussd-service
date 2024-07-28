package com.ussd.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UssdPage {
    private int page;
    private String message;
    private String type;
    private String events;
    private String dataLabel;
    private boolean terminalPage;
    private String moEngageEventName;
    private List<String> moeEngageEventAttributes;
}
