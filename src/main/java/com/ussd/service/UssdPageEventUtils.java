package com.ussd.service;

public class UssdPageEventUtils {

    public static void moveToPage(
            UssdPageService ussdService,
            String msisdn,
            String country,
            String messageLabel
    ) {
        ussdService.saveCustomerCurrentPage(msisdn, country, messageLabel);
    }
}
