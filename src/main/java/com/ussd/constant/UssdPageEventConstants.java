package com.ussd.constant;

public class UssdPageEventConstants {
    public static final String START_EVENT = "START";
    public static final String CONTINUE_EVENT = "CONTINUE";
    public static final String SESSION_TIMEOUT_TERMINATE_EVENT = "TERMINATE:SessionTimeOut";
    public static final String USER_INITIATED_TERMINATE_EVENT = "TERMINATE:UserInitiated";
    public static final String END_OF_MENU_TERMINATE_EVENT = "TERMINATE:EndOfMenu";

    // Numbers Constants
    public static final String ZERO = "0";
    public static final String DOUBLE_ZERO = "00";
    public static final String ONE = "1";
    public static final String TWO = "2";
    public static final String THREE = "3";
    public static final String FOUR = "4";
    public static final String FIVE = "5";
    public static final String SIX = "6";
    public static final String SEVEN = "7";
    public static final String EIGHT = "8";
    public static final String NINE = "9";
    public static final String TEN = "10";
    public static final String ASTERISK = "*";

    // Paging and Pagination Constants
    public static final String PAGE_ONE = "1";
    public static final String PAGE_TWO = "2";
    public static final String PAGE_THREE = "3";
    public static final String PAGE_FOUR = "4";
    public static final String PAGE_FIVE = "5";
    public static final String NEXT_PAGE = "99";
    public static final String INITIAL_PAGE = "#";
    public static final String PREVIOUS_PAGE = "0";
    public static final int ONE_ITEM_PER_PAGE = 1;
    public static final int TWO_ITEMS_PER_PAGE = 2;
    public static final int THREE_ITEMS_PER_PAGE = 3;
    public static final int FOUR_ITEMS_PER_PAGE = 4;
    public static final int FIVE_ITEMS_PER_PAGE = 5;
    public static final String PAGE_BOOKMARK_LABEL = "pageBookmark";
    public static final String BE_PROMO_CODE = "BE";

    public static final String ERROR_OCCURRED = "An error occurred while processing your input";
    public static final String LETSHEGO_THANK_YOU = "Thank you for choosing Letshego.";

    public static final String NO = "n";
    public static final String YES = "y";
    public static final String EVENT_STAGE_LABEL = "pageEventStage";
    public static final String SERVICE_UNAVAILABLE = "Service is currently unavailable.";
    public static final String USSD_START_EVENT_MESSAGE = "startMessage";

    public static final String WRONG_OPTION_SELECTED = "You have entered a wrong option. " +
            "Please dial the shortcode again and enter the correct option.";
    public static final String INCOMPLETE_SESSION = "You have a previously uncompleted session. " +
            "Do you want to continue from it:\n\n" +
            "1. Continue previous session\n2. Start from beginning.";
}
