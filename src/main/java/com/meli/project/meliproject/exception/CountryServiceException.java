package com.meli.project.meliproject.exception;

import org.springframework.http.HttpStatus;

import java.util.List;

public class CountryServiceException extends Exception {

    private final String code;
    private final List<String> messages;
    private final HttpStatus status;

    public CountryServiceException(String code, List<String> messages, HttpStatus status) {
        this.code = code;
        this.messages = messages;
        this.status = status;
    }

    public List<String> getMessages() {
        return messages;
    }

    public String getCode() {
        return code;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
