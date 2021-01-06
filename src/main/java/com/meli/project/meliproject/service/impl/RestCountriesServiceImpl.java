package com.meli.project.meliproject.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meli.project.meliproject.configuration.ConfigProperties;
import com.meli.project.meliproject.configuration.RestTemplateResponseErrorHandler;
import com.meli.project.meliproject.constants.ConstantValues;
import com.meli.project.meliproject.exception.RestCountriesServiceException;
import com.meli.project.meliproject.model.CountryInformation;
import com.meli.project.meliproject.service.IRestCountriesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

@Service("restCountriesService")
public class RestCountriesServiceImpl implements IRestCountriesService {

    private Logger logger;

    @PostConstruct
    public void init() {
        this.logger = LoggerFactory.getLogger(this.getClass());
    }

    private RestTemplate restTemplate;

    @Autowired
    private ConfigProperties configProperties;

    @Autowired
    public RestCountriesServiceImpl(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder
                .errorHandler(new RestTemplateResponseErrorHandler())
                .build();
    }

    @Override
    public CountryInformation getCountryInformationByName(String countryName) throws RestCountriesServiceException {

        logger.info("MELI-PROJECT : Consultando el servicio de REST Countries. ");

        HttpHeaders headers = new HttpHeaders();
        headers.set(ConstantValues.KeyProperty.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                .fromHttpUrl(configProperties.getRestCountries().concat(countryName));

        ResponseEntity<Object> response = restTemplate.exchange(
                uriComponentsBuilder.build().encode().toUri(),
                HttpMethod.GET,
                httpEntity,
                Object.class);

        if (response.getBody() == null || !HttpStatus.OK.equals(response.getStatusCode())
                || validateResponse(response)) {
            throw new RestCountriesServiceException(response.getStatusCode().name(),
                    Arrays.asList("No se ha obtenido información para el país " + countryName
                            + " correspondiente a la IP consultada. "), response.getStatusCode());
        }

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(((ArrayList) response.getBody()).get(0), CountryInformation.class);
    }

    private boolean validateResponse(ResponseEntity<Object> response) {

        return response.getBody() instanceof HashMap;
    }
}
