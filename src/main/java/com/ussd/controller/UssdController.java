package com.ussd.controller;

import com.ussd.constant.SupportedCountries;
import com.ussd.constant.TruTeqResponseType;
import com.ussd.constant.UssdPageDataLabels;
import com.ussd.constant.UssdPageEventConstants;
import com.ussd.constant.UssdProviderEvent;
import com.ussd.dto.request.TruRouteRequest;
import com.ussd.dto.response.Premium;
import com.ussd.dto.response.ProcessUssdPageResponse;
import com.ussd.dto.response.TruRouteResponse;
import com.ussd.enumclass.Country;
import com.ussd.service.HandleUssdEvent;
import com.ussd.service.UssdPageService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UssdController {

    private final UssdPageService ussdPageService;
    private final HandleUssdEvent handleUssdEvent;
    private static final Logger log = LoggerFactory.getLogger(UssdController.class);
    static final int COUNTRY_CODE_LENGTH = 3;
    static final String COUNTRY_CODE_PREFIX = "+";

    @PostMapping("/webhook/ussd/incoming-message")
    public TruRouteResponse handleIncomingUssdCode(@RequestBody TruRouteRequest request) {
        log.info("Request details dto request : {}", request.toString());
        var code = COUNTRY_CODE_PREFIX + request.getMsisdn().substring(COUNTRY_CODE_LENGTH);
        var country = Country.fromPhoneCode(code).name();
        return handleEvent(
                request.getMsisdn(),
                request.getSessionid(),
                request.getType(),
                request.getMsg()
        );
    }

    public TruRouteResponse handleEvent(String msisdn, String sessionId, String type, String msg) {
        String ussdEventType;
        switch (type) {
            case UssdPageEventConstants.ONE:
                ussdEventType = UssdProviderEvent.USSD_START_EVENT;
                break;

            case UssdPageEventConstants.TWO:
                ussdEventType = UssdProviderEvent.USSD_CONTINUE_EVENT;
                break;

            case UssdPageEventConstants.THREE:
                ussdEventType = UssdProviderEvent.USER_INITIATED_USSD_TERMINATE_EVENT;
                break;

            case UssdPageEventConstants.FOUR:
                ussdEventType = UssdProviderEvent.SESSION_TIMEOUT_USSD_TERMINATE_EVENT;
                break;

            default:
                throw new IllegalArgumentException("Unexpected value: " + type);
        }

        try {
            ussdPageService.saveCustomerFormDataField(
                    msisdn, SupportedCountries.UGANDA,
                    UssdPageDataLabels.IS_MSE_LOAN_COLLECTION,
                    UssdPageEventConstants.YES
            );

            ProcessUssdPageResponse processUssdPageResponse = handleUssdEvent.processMtnBillerUssdEventFun(
                    msisdn,
                    SupportedCountries.UGANDA,
                    sessionId,
                    msg,
                    ussdEventType
            );
            log.trace(processUssdPageResponse.getMessage());
            return new TruRouteResponse(
                    processUssdPageResponse.getShouldEndSession()
                            ? TruTeqResponseType.USSD_SESSION_CLOSE
                            : TruTeqResponseType.USSD_SESSION_OPEN,
                    processUssdPageResponse.getMessage(),
                    new Premium("", "")
            );
        } catch (Exception exception) {
            log.debug("Exception occurred : {}", exception.getMessage(), exception);
            return new TruRouteResponse(
                    TruTeqResponseType.USSD_SESSION_CLOSE,
                    UssdPageEventConstants.ERROR_OCCURRED + "." +
                            UssdPageEventConstants.LETSHEGO_THANK_YOU,
                    new Premium("", "")
            );
        }
    }
}
