package com.meli.project.meliproject.utils;

import com.meli.project.meliproject.constants.ConstantValues;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Arrays;

public class Utils {

    private Utils() {
    }

    public static HttpHeaders getHttpHeadersConfiguration() {

        HttpHeaders headers = new HttpHeaders();
        headers.set(ConstantValues.KeyProperty.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        return headers;
    }
}
