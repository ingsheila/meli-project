package com.meli.project.meliproject.service.impl;

import com.meli.project.meliproject.configuration.ConfigProperties;
import com.meli.project.meliproject.constants.ConstantValues;
import com.meli.project.meliproject.model.FixerResponse;
import com.meli.project.meliproject.service.IFixerService;
import com.meli.project.meliproject.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.util.HashMap;

@Service("fixerService")
public class FixerServiceImpl implements IFixerService {

    private Logger logger;

    @PostConstruct
    public void init() {
        this.logger = LoggerFactory.getLogger(this.getClass());
        getFixerInformation();
    }

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ConfigProperties configProperties;

    private FixerResponse fixerResponse;

    @Scheduled(cron = "0 */60 * * * *")
    private void getFixerInformation() {

        logger.info("MELI-PROJECT : Obteniendo el valor actual del euro. ");
        HttpEntity<Object> httpEntity = new HttpEntity<>(Utils.getHttpHeadersConfiguration());
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
            FixerResponse fixerResponseDefault = new FixerResponse();
            fixerResponseDefault.setRates(new HashMap<>());
            setFixerResponse(fixerResponseDefault);
        }
        setFixerResponse(response.getBody());
    }

    @Cacheable("rates")
    @Override
    public Double getRate(String currency) {
        return getFixerResponse().getRates().get(currency);
    }

    public FixerResponse getFixerResponse() {
        return fixerResponse;
    }

    public void setFixerResponse(FixerResponse fixerResponse) {
        this.fixerResponse = fixerResponse;
    }
}
