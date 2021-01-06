package com.meli.project.meliproject.service.impl;

import com.meli.project.meliproject.configuration.ConfigProperties;
import com.meli.project.meliproject.configuration.RestTemplateResponseErrorHandler;
import com.meli.project.meliproject.constants.ConstantValues;
import com.meli.project.meliproject.exception.CountryServiceException;
import com.meli.project.meliproject.model.CountryData;
import com.meli.project.meliproject.service.IIPCountryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@Service("iPCountryService")
public class IPCountryServiceImpl implements IIPCountryService {

    private Logger logger;

    @PostConstruct
    public void init() {
        this.logger = LoggerFactory.getLogger(this.getClass());
    }

    private RestTemplate restTemplate;

    @Autowired
    private ConfigProperties configProperties;

    @Autowired
    public IPCountryServiceImpl(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder
                .errorHandler(new RestTemplateResponseErrorHandler())
                .build();
    }

    @Override
    public CountryData getCountryDataByIP(String ipAddress) throws CountryServiceException {

        logger.info("MELI-PROJECT : Consultando el servicio de IP2Country. ");

        HttpHeaders headers = new HttpHeaders();
        headers.set(ConstantValues.KeyProperty.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(configProperties.getIpCountry().concat(ipAddress));

        ResponseEntity<CountryData> response = restTemplate.exchange(
                uriComponentsBuilder.build().encode().toUri(),
                HttpMethod.GET,
                httpEntity,
                CountryData.class);

        if (response.getBody() == null || !HttpStatus.OK.equals(response.getStatusCode()) || validateResponse(response)) {
            throw new CountryServiceException(response.getStatusCode().name(),
                    Arrays.asList("No se ha obtenido información para la IP consultada. "), response.getStatusCode());
        }
        return response.getBody();
    }

    private boolean validateResponse(ResponseEntity<CountryData> countryData) {

        return countryData != null && countryData.getBody() != null
                && countryData.getBody().getCountryName().equalsIgnoreCase(ConstantValues.EMPTY_STRING);
    }
}
