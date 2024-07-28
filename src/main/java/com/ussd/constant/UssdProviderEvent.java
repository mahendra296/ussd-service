package com.ussd.constant;

public class UssdProviderEvent {
    public static final String USSD_START_EVENT = "START";
    public static final String USSD_CONTINUE_EVENT = "CONTINUE";
    public static final String USSD_END_EVENT = "END";
    public static final String SESSION_TIMEOUT_USSD_TERMINATE_EVENT = "TERMINATE:SessionTimeOut";
    public static final String USER_INITIATED_USSD_TERMINATE_EVENT = "TERMINATE:UserInitiated";
    public static final String END_OF_MENU_USSD_TERMINATE_EVENT = "TERMINATE:EndOfMenu";
}
