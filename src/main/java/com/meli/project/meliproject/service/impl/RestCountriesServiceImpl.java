package com.meli.project.meliproject.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meli.project.meliproject.configuration.ConfigProperties;
import com.meli.project.meliproject.exception.RestCountriesServiceException;
import com.meli.project.meliproject.model.CountryInformation;
import com.meli.project.meliproject.service.IRestCountriesService;
import com.meli.project.meliproject.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.util.*;

@Service("restCountriesService")
public class RestCountriesServiceImpl implements IRestCountriesService {

    private Logger logger;

    @PostConstruct
    public void init() {
        this.logger = LoggerFactory.getLogger(this.getClass());
    }

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ConfigProperties configProperties;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public CountryInformation getCountryInformationByName(String countryName, String countryCode) throws RestCountriesServiceException {

        logger.info("MELI-PROJECT : Consultando el servicio de REST Countries. ");
        HttpEntity<Object> httpEntity = new HttpEntity<>(Utils.getHttpHeadersConfiguration());
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

        List<Object> objectList = (ArrayList) response.getBody();
        if (objectList.size() > 1) {
            return getCountryInformation(objectList, countryCode);
        } else {
            return this.objectMapper.convertValue(objectList.get(0), CountryInformation.class);
        }
    }

    private boolean validateResponse(ResponseEntity<Object> response) {
        return response.getBody() instanceof HashMap;
    }

    private CountryInformation getCountryInformation(List<Object> objectList, String countryCode) {

        List<CountryInformation> countryInformationList = new ArrayList<>();
        objectList.forEach(object -> countryInformationList.add(this.objectMapper.convertValue(object, CountryInformation.class)));

        Optional<CountryInformation> optionalCountryInformation = countryInformationList.stream().filter(countryInformation
                -> countryInformation.getAlpha3Code().equalsIgnoreCase(countryCode)).findFirst();
        return optionalCountryInformation.isPresent() ? optionalCountryInformation.get() : new CountryInformation();
    }
}
