package com.ussd.interfaces.event;

import com.ussd.dto.response.ProcessUssdPageResponse;
import com.ussd.dto.response.UssdPage;

public interface IPageEvent {

    String getName();
    ProcessUssdPageResponse processPageEvent(String msisdn, String country, UssdPage page);
}
