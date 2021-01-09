package com.meli.project.meliproject.service;


import com.meli.project.meliproject.configuration.ConfigProperties;
import com.meli.project.meliproject.exception.CountryServiceException;
import com.meli.project.meliproject.model.CountryData;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(locations = "classpath:application-test.properties")
public class IPCountryServiceTest {

    @Autowired
    private ConfigProperties configProperties;

    @MockBean
    private RestTemplate restTemplate;

    @MockBean
    private IFixerService fixerService;

    @Autowired
    private IIPCountryService ipCountryService;

    private static final String IP_ADDRESS = "100.6.7.8";
    private static final String EMPTY_STRING = "";

    @Test
    public void getCountryDataByIP() throws CountryServiceException {

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                .fromHttpUrl(configProperties.getIpCountry().concat(IP_ADDRESS));

        Mockito.when(restTemplate.exchange(Mockito.eq(uriComponentsBuilder.build().encode().toUri()),
                Mockito.eq(HttpMethod.GET),
                Mockito.any(),
                Mockito.eq(CountryData.class))).thenReturn(generateCountryDataResponse());

        CountryData countryData = this.ipCountryService.getCountryDataByIP(IP_ADDRESS);

        assertEquals("United States", countryData.getCountryName());
        assertEquals("USA", countryData.getCountryCode3());
        assertEquals("US", countryData.getCountryCode());
        assertEquals("us", countryData.getCountryEmoji());
    }

    @Test(expected = CountryServiceException.class)
    public void getCountryServiceException() throws CountryServiceException {

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                .fromHttpUrl(configProperties.getIpCountry().concat(IP_ADDRESS));

        Mockito.when(restTemplate.exchange(Mockito.eq(uriComponentsBuilder.build().encode().toUri()),
                Mockito.eq(HttpMethod.GET),
                Mockito.any(),
                Mockito.eq(CountryData.class))).thenReturn(generateErrorResponse());

        this.ipCountryService.getCountryDataByIP(IP_ADDRESS);
    }

    @Test(expected = CountryServiceException.class)
    public void getEmptyCountryData() throws CountryServiceException {

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                .fromHttpUrl(configProperties.getIpCountry().concat(IP_ADDRESS));

        Mockito.when(restTemplate.exchange(Mockito.eq(uriComponentsBuilder.build().encode().toUri()),
                Mockito.eq(HttpMethod.GET),
                Mockito.any(),
                Mockito.eq(CountryData.class))).thenReturn(generateEmptyCountryDataResponse());

        this.ipCountryService.getCountryDataByIP(IP_ADDRESS);
    }

    @Test(expected = CountryServiceException.class)
    public void getNullBodyResponse() throws CountryServiceException {

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                .fromHttpUrl(configProperties.getIpCountry().concat(IP_ADDRESS));

        Mockito.when(restTemplate.exchange(Mockito.eq(uriComponentsBuilder.build().encode().toUri()),
                Mockito.eq(HttpMethod.GET),
                Mockito.any(),
                Mockito.eq(CountryData.class))).thenReturn(generateNullBodyResponse());

        this.ipCountryService.getCountryDataByIP(IP_ADDRESS);
    }

    private ResponseEntity<CountryData> generateCountryDataResponse() {

        CountryData countryData = new CountryData();
        countryData.setCountryName("United States");
        countryData.setCountryCode("US");
        countryData.setCountryCode3("USA");
        countryData.setCountryEmoji("us");
        return ResponseEntity.status(HttpStatus.OK).body(countryData);
    }

    private ResponseEntity<CountryData> generateEmptyCountryDataResponse() {

        CountryData countryData = new CountryData();
        countryData.setCountryName(EMPTY_STRING);
        countryData.setCountryCode(EMPTY_STRING);
        countryData.setCountryCode3(EMPTY_STRING);
        countryData.setCountryEmoji(EMPTY_STRING);
        return ResponseEntity.status(HttpStatus.OK).body(countryData);
    }

    private ResponseEntity<CountryData> generateErrorResponse() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CountryData());
    }

    private ResponseEntity<CountryData> generateNullBodyResponse() {
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
