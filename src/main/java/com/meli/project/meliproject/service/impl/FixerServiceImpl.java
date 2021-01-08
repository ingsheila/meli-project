package com.meli.project.meliproject.service.impl;

import com.meli.project.meliproject.configuration.ConfigProperties;
import com.meli.project.meliproject.configuration.RestTemplateResponseErrorHandler;
import com.meli.project.meliproject.constants.ConstantValues;
import com.meli.project.meliproject.model.FixerResponse;
import com.meli.project.meliproject.service.IFixerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@Service("fixerService")
public class FixerServiceImpl implements IFixerService {

    private Logger logger;

    @PostConstruct
    public void init() {
        this.logger = LoggerFactory.getLogger(this.getClass());
        getFixerInformation();
    }

    private RestTemplate restTemplate;

    @Autowired
    private ConfigProperties configProperties;

    private FixerResponse fixerResponse;

    @Autowired
    public FixerServiceImpl(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder
                .errorHandler(new RestTemplateResponseErrorHandler())
                .build();
    }

    @Scheduled(cron = "0 */60 * * * *")
    private void getFixerInformation() {

        logger.info("MELI-PROJECT : Obteniendo el valor actual del euro. ");

        HttpHeaders headers = new HttpHeaders();
        headers.set(ConstantValues.KeyProperty.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                .fromHttpUrl(configProperties.getDataFixer())
                .queryParam(ConstantValues.KeyProperty.ACCESS_KEY, configProperties.getApiKey())
                .queryParam(ConstantValues.KeyProperty.FORMAT, 1);

        ResponseEntity<FixerResponse> response = restTemplate.exchange(
                uriComponentsBuilder.build().encode().toUri(),
                HttpMethod.GET,
                httpEntity,
                FixerResponse.class);

        if (!response.getBody().isSuccess()) {
            logger.info("MELI-PROJECT : No se ha podido obtener el valor actual del euro. ");
        }
        setFixerResponse(response.getBody());
    }

    @Cacheable("rates")
    @Override
    public Long getRate(String currency) {
        return getFixerResponse().getRates().get(currency);
    }

    public FixerResponse getFixerResponse() {
        return fixerResponse;
    }

    public void setFixerResponse(FixerResponse fixerResponse) {
        this.fixerResponse = fixerResponse;
    }
}
