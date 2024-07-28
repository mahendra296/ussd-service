package com.ussd.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class APIResult<T> {
    private boolean success;
    private String message;
    private T data;
    private ArrayList<Error> errors;

    public static <T> APIResult<T> success(String message, T data) {
        return new APIResult<>(true, message, data, new ArrayList<>());
    }

    public static <T> APIResult<T> fail(String exception) {
        ArrayList<Error> errors = new ArrayList<>();
        errors.add(new Error("error"));
        return new APIResult<>(false, exception, null, errors);
    }
}
