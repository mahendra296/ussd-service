package com.ussd.repository;

import java.util.Map;

public interface RedisRepository {
    Object getValue(String key);
    void setValue(String key, String value);
    void clearValue(String key);
    void saveHashField(String key, String fieldKey, String value);
    String getHashField(String key, String fieldKey);
    void saveHashValues(String key, Map<String,String> valuesMap);
    Map<String, String> getHashValues(String key);
    void deleteHashField(String key, String fieldKey);
    void deleteHash(String key);
}
