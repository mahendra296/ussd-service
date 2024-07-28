package com.ussd.interfaces.event.impl;

import com.ussd.dto.response.ProcessUssdPageResponse;
import com.ussd.dto.response.UssdPage;
import com.ussd.interfaces.event.IPageEvent;
import com.ussd.service.UssdPageService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClearCustomerDataEvent implements IPageEvent {
    private static final Logger log = LoggerFactory.getLogger(ClearCustomerDataEvent.class);
    private final UssdPageService ussdPageService;
    @Override
    public String getName() {
        return "clearCustomerData";
    }

    @Override
    public ProcessUssdPageResponse processPageEvent(String msisdn, String country, UssdPage page) {
        try {
            // clear collected user data from Redis
            log.info("Clearing all the cached data for the customer from Redis");
            ussdPageService.clearCustomerCurrentPage(msisdn, country);
            ussdPageService.clearCustomerFormDataFields(msisdn, country);
        } catch(Exception ex) {
            log.error(ex.getMessage()); // log error here
        }
        return null;
    }


}
