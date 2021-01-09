package com.meli.project.meliproject.service;


import com.meli.project.meliproject.configuration.ConfigProperties;
import com.meli.project.meliproject.constants.ConstantValues;
import com.meli.project.meliproject.exception.RestCountriesServiceException;
import com.meli.project.meliproject.model.CountryInformation;
import com.meli.project.meliproject.model.FixerResponse;
import com.meli.project.meliproject.service.impl.FixerServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(locations = "classpath:application-test.properties")
public class FixerServiceTest {

    @Autowired
    private ConfigProperties configProperties;

    @MockBean
    private RestTemplate restTemplate;

    @MockBean
    private IFixerService fixerService;

    private static final String CURRENCY_ARS = "ARS";
    private static final String CURRENCY_USD = "USD";
    private static final String CURRENCY_BHD = "BHD";

    @Test
    public void getRate()  {

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                .fromHttpUrl(configProperties.getDataFixer())
                .queryParam(ConstantValues.KeyProperty.ACCESS_KEY, configProperties.getApiKey())
                .queryParam(ConstantValues.KeyProperty.FORMAT, 1);

        Mockito.when(restTemplate.exchange(Mockito.eq(uriComponentsBuilder.build().encode().toUri()),
                Mockito.eq(HttpMethod.GET),
                Mockito.any(),
                Mockito.eq(FixerResponse.class))).thenReturn(generateFixerResponse());

        assertEquals(104.0, fixerService.getRate(CURRENCY_ARS));
        assertEquals(1.2225, fixerService.getRate(CURRENCY_USD));
        assertEquals(0.46087, fixerService.getRate(CURRENCY_BHD));
    }

    private ResponseEntity<FixerResponse> generateFixerResponse(){

        FixerResponse fixerResponse = new FixerResponse();
        fixerResponse.setSuccess(true);
        fixerResponse.setRates(new HashMap<>());
        fixerResponse.getRates().put(CURRENCY_USD,1.2225);
        fixerResponse.getRates().put(CURRENCY_BHD,0.46087);
        fixerResponse.getRates().put(CURRENCY_ARS,Double.valueOf(104));
        return ResponseEntity.status(HttpStatus.OK).body(fixerResponse);
    }
}
