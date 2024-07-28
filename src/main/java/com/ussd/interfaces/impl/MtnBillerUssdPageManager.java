package com.ussd.interfaces.impl;

import com.google.gson.Gson;
import com.ussd.constant.SupportedCountries;
import com.ussd.constant.UssdPageDataLabels;
import com.ussd.constant.UssdPageEventConstants;
import com.ussd.constant.UssdPageLabels;
import com.ussd.constant.UssdProviderEvent;
import com.ussd.dto.response.ProcessUssdPageResponse;
import com.ussd.dto.response.UssdPage;
import com.ussd.enumclass.UssdPageType;
import com.ussd.interfaces.HandleMtnBillerUssdEventProvider;
import com.ussd.interfaces.event.IPageEvent;
import com.ussd.interfaces.event.impl.ClearCustomerDataEvent;
import com.ussd.service.UssdPageService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MtnBillerUssdPageManager implements HandleMtnBillerUssdEventProvider {

    private static final Logger log = LoggerFactory.getLogger(MtnBillerUssdPageManager.class);

    private final UssdPageService ussdPageService;
    private final ClearCustomerDataEvent clearCustomerDataEvent;
    private final List<IPageEvent> events;
    private HashMap<String, UssdPage> ugandaUssdMenusMap = new HashMap<>();
    private HashMap<String, IPageEvent> eventMap = new HashMap<>();

    @PostConstruct
    public void init() {
        this.ugandaUssdMenusMap = loadPageData(SupportedCountries.UGANDA);
        loadEvents();
    }

    public HashMap<String, UssdPage> loadPageData(String country) {
        // Get the resource file
        try {
            InputStream file = getClass().getClassLoader()
                    .getResourceAsStream("menu/" + country + ".json");

            if (file == null) {
                throw new IllegalArgumentException("Resource not found: menu/" + country + ".json");
            }

            // Create a reader
            Reader reader = new InputStreamReader(file, StandardCharsets.UTF_8);

            // Define the type for Gson
            Type type = new com.google.common.reflect.TypeToken<HashMap<String, UssdPage>>() {
            }.getType();

            // Parse JSON
            return new Gson().fromJson(reader, type);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void loadEvents() {
        // Load the ussd page events here
        log.info("Loading the ussd messages events into memory");
        for (IPageEvent event : events) {
            eventMap.put(event.getName(), event);
        }
        log.info("Finished loading the ussd messages events into memory");
    }

    @Override
    public ProcessUssdPageResponse processUssdRequest(String msisdn, String country, String ussdSessionId, String ussdInput, String ussdEvent) {
        String pageLabel = ussdPageService.getCustomerCurrentPage(msisdn, country);

        // Process the event based on the ussdEvent type
        if (UssdProviderEvent.USSD_START_EVENT.equals(ussdEvent)) {
            // Handle the start event
            ussdPageService.saveCustomerFormDataField(
                    msisdn,
                    country,
                    UssdPageEventConstants.USSD_START_EVENT_MESSAGE,
                    ussdInput
            );

            return handleUssdRequestStartEvent(msisdn, country, pageLabel);
        } else if (UssdProviderEvent.USSD_CONTINUE_EVENT.equals(ussdEvent)) {
                return handleUssdRequestContinueEvent(msisdn, country, pageLabel, ussdInput);
        } else {
            // Must be a TERMINATE event
            return handleUssdRequestTerminateEvent(msisdn, country, pageLabel, ussdEvent);
        }
    }

    public ProcessUssdPageResponse handleUssdRequestStartEvent(
            String msisdn,
            String country,
            String pageLabel) {

        // Check if a previous session exists
        if (pageLabel != null && !pageLabel.isEmpty()) {
            // Session continue page
            return continueSessionMenuPage(msisdn, country);
        }

        // Get default start (main first) page label
        String startPageLabel = getCustomerStartMenu(msisdn, country);

        // Continue the processing algorithm here
        UssdPage ussdPage = getUssdMenuPage(country, startPageLabel);
        if (ussdPage == null) {
            return serviceUnavailableMessage(msisdn);
        }

        // Fire the menu page events here
        ProcessUssdPageResponse ussdPageResponse = firePageEvents(msisdn, country, ussdPage);
        if (ussdPageResponse != null) {
            return ussdPageResponse;
        }

        // If this is a terminal page, do Redis clean up
        if (ussdPage.isTerminalPage()) {
            clearCustomerDataEvent.processPageEvent(msisdn, country, ussdPage);
        }

        // Return the first/start page
        return new ProcessUssdPageResponse(
                msisdn,
                ussdPage.getMessage().toString(),
                ussdPage.isTerminalPage()
        );
    }

    public ProcessUssdPageResponse handleUssdRequestTerminateEvent(
            String msisdn,
            String country,
            String pageLabel,
            String ussdEvent) {

        // Check if it was user-initiated terminate, session timeout, or end of menu
        // If end of menu, run the clean-up activities here.
        // Like cleaning up the Redis hash and data for the customer
        if (UssdProviderEvent.END_OF_MENU_USSD_TERMINATE_EVENT.equals(ussdEvent)) {
            // Clear collected user data from Redis
            UssdPage clearUssdPage = getUssdMenuPage(
                    country, UssdPageLabels.MAIN
            );

            if (clearUssdPage != null) {
                clearCustomerDataEvent.processPageEvent(
                        msisdn, country, clearUssdPage
                ); // Fire page event
            }
        }

        return sessionEndMessage(msisdn);
    }

    public ProcessUssdPageResponse continueSessionMenuPage(
            String msisdn,
            String country) {

        // Since previous session is not empty, prompt the customer to continue from session
        ussdPageService.saveCustomerFormDataField(
                msisdn,
                country,
                UssdPageDataLabels.CONTINUE_SESSION,
                UssdPageEventConstants.YES
        );

        return new ProcessUssdPageResponse(
                msisdn,
                UssdPageEventConstants.INCOMPLETE_SESSION,
                false
        );
    }

    public String getCustomerStartMenu(String msisdn, String country) {
        // Get the begin session ussd page
        UssdPage beginSessionEventsUssdPage = getUssdMenuPage(
                country,
                UssdPageLabels.BEGIN_MTN_SESSION_EVENTS
        );

        // Fire the begin session events
        firePageEvents(msisdn, country, beginSessionEventsUssdPage);

        // Return the correct start menu
        return ussdPageService.getCustomerCurrentPage(msisdn, country);
    }

    public UssdPage getUssdMenuPage(String country, String pageLabel) {
        // Return the menu for the customer's country
        UssdPage ussdPage = null;
        if (SupportedCountries.UGANDA.equals(country)) {
            ussdPage = ugandaUssdMenusMap.get(pageLabel);
        }
        return ussdPage;
    }

    public ProcessUssdPageResponse firePageEvents(
            String msisdn,
            String country,
            UssdPage ussdPage) {

        // Fire the menu page events here
        List<String> pageEventNames = new ArrayList<>();
        if (ussdPage.getEvents() != null && !ussdPage.getEvents().isEmpty()) {
            String[] eventsArray = ussdPage.getEvents().split(",");
            for (String event : eventsArray) {
                pageEventNames.add(event.trim());
            }
        }

        IPageEvent eventObject;
        ProcessUssdPageResponse eventResponse = null;

        for (String eventName : pageEventNames) {
            // Get event
            eventObject = eventMap.get(eventName);
            if (eventObject != null) {
                log.trace("Firing the ussd page event with name: {}", eventObject.getName());
                eventResponse = eventObject.processPageEvent(msisdn, country, ussdPage);
            }

            if (eventResponse != null) {
                log.trace("Fired ussd page event '{}' returned a response", eventObject != null ? eventObject.getName() : "null");
                // Terminate and return the error/message to the customer
                return eventResponse;
            }

            String terminateEvent = ussdPageService.getCustomerFormDataField(msisdn, country, "terminate_page_events");
            if ("true".equals(terminateEvent)) {
                ussdPageService.clearCustomerFormDataField(msisdn, country, "terminate_page_events");
                return null;
            }
        }
        // No event terminated the ussd menu flow
        return null;
    }

    public ProcessUssdPageResponse serviceUnavailableMessage(String msisdn) {
        log.trace("Service is currently unavailable");

        String message = UssdPageEventConstants.SERVICE_UNAVAILABLE + ". " + UssdPageEventConstants.LETSHEGO_THANK_YOU;

        return new ProcessUssdPageResponse(
                msisdn,
                message,
                true
        );
    }

    public ProcessUssdPageResponse sessionEndMessage(String msisdn) {
        // Return a new instance of ProcessUssdPageResponse with given parameters
        return new ProcessUssdPageResponse(
                msisdn,
                UssdPageEventConstants.LETSHEGO_THANK_YOU,
                true
        );
    }

    public ProcessUssdPageResponse handleUssdRequestContinueEvent(
            String msisdn,
            String country,
            String passedPageLabel,
            String ussdInput) {

        // Get the session continuation flag
        String sessionContinue = ussdPageService.getCustomerFormDataField(
                msisdn, country, UssdPageDataLabels.CONTINUE_SESSION
        );

        String pageLabel = passedPageLabel;

        UssdPage ussdPage = this.getUssdMenuPage(country, pageLabel);

        if (ussdPage == null) {
            return this.wrongOptionSelectedMessage(msisdn);
        }

        if (UssdPageType.INPUT.getValue().equals(ussdPage.getType())) {
            // Save the input for the page
            ussdPageService.saveCustomerFormDataField(
                    msisdn,
                    country,
                    ussdPage.getDataLabel(),
                    ussdInput
            );
        }

        String pageEvent = this.getPageEventStage(ussdPageService, msisdn, country);

        ProcessUssdPageResponse ussdPageResponse = this.firePageEvents(msisdn, country, ussdPage);
        if (ussdPageResponse != null) {
            return ussdPageResponse;
        }

        // Any update to current page flow?
        pageLabel = ussdPageService.getCustomerCurrentPage(msisdn, country);

        // Fetch and return next page in the menu flow
        return this.getResponseForNextPage(msisdn, country, pageLabel, ussdInput);
    }

    public ProcessUssdPageResponse wrongOptionSelectedMessage(String msisdn) {
        log.trace("Entered wrong option on USSD page menu");
        return new ProcessUssdPageResponse(
                msisdn,
                UssdPageEventConstants.WRONG_OPTION_SELECTED,
                true
        );
    }

    public String getPageEventStage(UssdPageService ussdPageService, String msisdn, String country) {
        String pageEventStage = ussdPageService.getCustomerFormDataField(msisdn, country, UssdPageEventConstants.EVENT_STAGE_LABEL);
        return pageEventStage != null ? pageEventStage : "1";
    }

    public ProcessUssdPageResponse getResponseForNextPage(
            String msisdn,
            String country,
            String pageLabel,
            String ussdInput
    ) {
        // Fetch next page from the menu list
        String nextPageLabel = getNextUssdMenuPageLabel(country, pageLabel, ussdInput);

        // Check whether this was a menu switch operation
        String menuSwitch = ussdPageService.getCustomerFormDataField(msisdn, country, UssdPageDataLabels.MENU_SWITCH);
        if (menuSwitch != null && !menuSwitch.isEmpty() && UssdPageEventConstants.YES.equals(menuSwitch)) {
            nextPageLabel = pageLabel; // Set as the current page label
            // Clear it from Redis
            ussdPageService.clearCustomerFormDataField(msisdn, country, UssdPageDataLabels.MENU_SWITCH);
        }

        UssdPage nextUssdPage = getUssdMenuPage(country, nextPageLabel);
        if (nextUssdPage == null) {
            return wrongOptionSelectedMessage(msisdn);
        }

        // Move the customer's USSD session to the next page
        ussdPageService.saveCustomerCurrentPage(msisdn, country, nextPageLabel);

        // If the next page is a terminate page, fire the events for that page
        if (nextUssdPage.isTerminalPage()) {
            ProcessUssdPageResponse terminatePageResponse = firePageEvents(msisdn, country, nextUssdPage);
            if (terminatePageResponse != null) {
                return terminatePageResponse;
            }
            // Clear collected user data from Redis
            clearCustomerDataEvent.processPageEvent(msisdn, country, nextUssdPage); // Fire page event
        }

        // Should return the next page
        return new ProcessUssdPageResponse(
                msisdn,
                nextUssdPage.getMessage(),
                nextUssdPage.isTerminalPage()
        );
    }

    public String getNextUssdMenuPageLabel(
            String country,
            String pageLabel,
            String ussdPageInput
    ) {
        // Fetch next page from the menu list
        UssdPage ussdPage = getUssdMenuPage(country, pageLabel);
        if (ussdPage == null) {
            return null;
        }

        if (UssdPageType.OPTION.getValue().equals(ussdPage.getType())) {
            return pageLabel + "_" + (ussdPageInput != null ? ussdPageInput.trim() : "");
        } else {
            return pageLabel + "_1";
        }
    }
}
