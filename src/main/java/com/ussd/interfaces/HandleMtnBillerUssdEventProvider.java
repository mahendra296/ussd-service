package com.ussd.interfaces;

import com.ussd.dto.response.ProcessUssdPageResponse;

public interface HandleMtnBillerUssdEventProvider {
    ProcessUssdPageResponse processUssdRequest(
            String msisdn,
            String country,
            String ussdSessionId,
            String ussdInput,
            String ussdEvent
    );
}
