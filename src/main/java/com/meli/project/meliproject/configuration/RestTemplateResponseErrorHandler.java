package com.meli.project.meliproject.configuration;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

@Component
public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse httpResponse) throws IOException {
        return (httpResponse.getStatusCode().series() == HttpStatus.FORBIDDEN.series()
                || httpResponse.getStatusCode().series() == HttpStatus.BAD_REQUEST.series()
                || httpResponse.getStatusCode().series() == HttpStatus.INTERNAL_SERVER_ERROR.series()
                || httpResponse.getStatusCode().series() == HttpStatus.UNAUTHORIZED.series()
                || httpResponse.getStatusCode().series() == HttpStatus.NOT_FOUND.series()
                || httpResponse.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR);
    }

    @Override
    public void handleError(ClientHttpResponse httpResponse) throws IOException {
        if (httpResponse.getStatusCode() == HttpStatus.NO_CONTENT) {
            throw new IOException();
        }
    }

}
