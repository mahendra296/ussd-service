package com.ussd.service;

import com.ussd.enumclass.RedisKeyPrefix;
import com.ussd.repository.RedisRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UssdPageService {

    private final RedisRepository redisRepository;
    private static final Logger log = LoggerFactory.getLogger(UssdPageService.class);

    public String getCustomerCurrentPage(String msisdn, String country) {
        var redisKey = RedisKeyPrefix.SESSION.getValue() + country + ":" + msisdn;
        var pageLabel = redisRepository.getValue(redisKey);
        if (pageLabel != null) {
            log.info("Fetched cached customer current index data from redis : {}", pageLabel);
            return pageLabel.toString();
        }
        return null;
    }

    public void saveCustomerCurrentPage(String msisdn, String country, String pageLabel) {
        // construct the redis set's key
        var redisKey = RedisKeyPrefix.SESSION.getValue() + country + ":" + msisdn;
        // save the session's current index label
        redisRepository.setValue(redisKey, pageLabel);
        log.info("Cached customer index label into Redis as {}", pageLabel);
    }

    public void clearCustomerCurrentPage(String msisdn, String country) {
        // construct the redis set's key
        var redisKey = RedisKeyPrefix.SESSION.getValue() + country + ":" + msisdn;
        // clear the session's current index label
        redisRepository.clearValue(redisKey);
    }

    public String getCustomerFormDataField(String msisdn, String country, String dataLabel) {
        // construct the redis set's key
        var redisKey = RedisKeyPrefix.COLLECTED_DATA.getValue() + country + ":" + msisdn;
        // fetch the collected input data
        var result = redisRepository.getHashField(redisKey, dataLabel);
        return (Objects.equals(result, "null")) ? null : result;
    }

    public void saveCustomerFormDataField(String msisdn, String country, String dataLabel, String dataValue) {
        // construct the redis set's key
        var redisKey = RedisKeyPrefix.COLLECTED_DATA.getValue() + country + ":" + msisdn;
        // save the collected customer input value to Redis
        redisRepository.saveHashField(redisKey, dataLabel, dataValue);
        log.info("Cached the customer form field with key : {} into Redis", dataLabel);
    }

    public void clearCustomerFormDataField(String msisdn, String country, String dataLabel) {
        // construct the redis set's key
        var redisKey = RedisKeyPrefix.COLLECTED_DATA.getValue() + country + ":" + msisdn;
        // removed the collected customer form data input from Redis
        redisRepository.deleteHashField(redisKey, dataLabel);
        log.info("Cleared the cached customer form field with key: {} from Redis", dataLabel);
    }

    public void clearCustomerFormDataFields(String msisdn, String country) {
        // construct the redis set's key
        var redisKey = RedisKeyPrefix.COLLECTED_DATA.getValue() + country + ":" + msisdn;
        // removed the collected customer form data input from Redis
        redisRepository.deleteHash(redisKey);
    }

    public void saveCustomerFormData(String msisdn, String country, String action, Map<String, String> dataValues) {
        // construct the redis set's key
        var redisKey = RedisKeyPrefix.SESSION.getValue() + country + ":" + msisdn + ":" + action;
        // save the session's current index label
        redisRepository.saveHashValues(redisKey, dataValues);
    }

    public Map<String, String> getCustomerFormData(String msisdn, String country, String action) {
        // construct the redis set's key
        var redisKey = RedisKeyPrefix.SESSION.getValue() + country + ":" + msisdn + ":" + action;
        return redisRepository.getHashValues(redisKey);
    }
}
