package com.ussd.service;

// import com.ussd.controller.UssdController;
import com.ussd.dto.response.ProcessUssdPageResponse;
import com.ussd.interfaces.HandleMtnBillerUssdEventProvider;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HandleUssdEvent {

    private final HandleMtnBillerUssdEventProvider handleMtnBillerUssdEventProvider;
    private static final Logger log = LoggerFactory.getLogger(HandleUssdEvent.class);
    public ProcessUssdPageResponse processMtnBillerUssdEventFun(
            String msisdn,
            String country,
            String ussdSessionId,
            String ussdInput,
            String ussdEvent) {
        try {
            return handleMtnBillerUssdEventProvider.processUssdRequest(
                    msisdn,
                    country,
                    ussdSessionId,
                    ussdInput,
                    ussdEvent
            );
        } catch (Exception exception) {
            log.error("Exception occurred : {}", exception.getMessage());
            throw new RuntimeException(exception.getMessage());
        }
    }
}
