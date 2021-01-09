package com.meli.project.meliproject.service;

import com.meli.project.api.model.TraceRequest;
import com.meli.project.api.model.TraceResponse;
import com.meli.project.meliproject.constants.ConstantValues;
import com.meli.project.meliproject.exception.ContextualInformationException;
import com.meli.project.meliproject.exception.CountryServiceException;
import com.meli.project.meliproject.exception.RestCountriesServiceException;
import com.meli.project.meliproject.model.CountryData;
import com.meli.project.meliproject.model.CountryInformation;
import com.meli.project.meliproject.model.Currency;
import com.meli.project.meliproject.model.Language;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(locations = "classpath:application-test.properties")
public class ContextualInformationServiceTest {

    @MockBean
    private IFixerService fixerService;

    @MockBean
    private IIPCountryService ipCountryService;

    @MockBean
    private IRestCountriesService restCountriesService;

    @MockBean
    private IMongoDBService mongoDBService;

    @Autowired
    private IContextualInformationService contextualInformationService;

    private static final String FRANCE_IP_ADDRESS = "163.172.7.80";
    private static final String INVALID_IP_ADDRESS = "163.172.7";

    @Test
    public void getTraceInformation() throws CountryServiceException, RestCountriesServiceException, ContextualInformationException {

        Mockito.when(this.ipCountryService.getCountryDataByIP(anyString())).thenReturn(generateCountryDataResponse());
        Mockito.when(this.restCountriesService.getCountryInformationByName(anyString(), anyString())).thenReturn(generateCountryInformation());
        Mockito.when(this.fixerService.getRate(anyString())).thenReturn(104.2);

        TraceResponse traceResponse = this.contextualInformationService.getTraceInformation(generateTraceRequest(FRANCE_IP_ADDRESS));
        assertEquals("France", traceResponse.getData().getCountry());
        assertEquals("EUR (1 EUR = 104.2 EUR)", traceResponse.getData().getCurrency());
        assertEquals(FRANCE_IP_ADDRESS, traceResponse.getData().getIp());
        assertEquals("fr", traceResponse.getData().getIsoCode());
        assertEquals("French (fr)", traceResponse.getData().getLanguages().get(0));
        assertEquals(4, traceResponse.getData().getTimes().size());
        assertEquals("UTC-10:00", traceResponse.getData().getTimes().get(0));
        assertEquals("10823.68 kms", traceResponse.getData().getEstimatedDistance());
    }

    @Test(expected = ContextualInformationException.class)
    public void getTraceInformationWithInvalidIP() throws ContextualInformationException {

        this.contextualInformationService.getTraceInformation(generateTraceRequest(INVALID_IP_ADDRESS));
    }

    @Test(expected = ContextualInformationException.class)
    public void getTraceInformationWithoutIP() throws ContextualInformationException {

        this.contextualInformationService.getTraceInformation(generateTraceRequest(""));
    }

    @Test(expected = ContextualInformationException.class)
    public void getCountryServiceException() throws ContextualInformationException, CountryServiceException {

        Mockito.when(this.ipCountryService.getCountryDataByIP(anyString())).thenThrow(
                new ContextualInformationException(ConstantValues.NOT_FOUND, Arrays.asList("NOT FOUND"),
                        HttpStatus.NOT_FOUND));
        this.contextualInformationService.getTraceInformation(generateTraceRequest(FRANCE_IP_ADDRESS));
    }

    @Test(expected = ContextualInformationException.class)
    public void getRestCountriesServiceException() throws ContextualInformationException, CountryServiceException, RestCountriesServiceException {

        Mockito.when(this.ipCountryService.getCountryDataByIP(anyString())).thenReturn(generateCountryDataResponse());
        Mockito.when(this.restCountriesService.getCountryInformationByName(anyString(), anyString())).thenThrow(
                new ContextualInformationException(ConstantValues.NOT_FOUND, Arrays.asList("NOT FOUND"),
                        HttpStatus.NOT_FOUND));
        this.contextualInformationService.getTraceInformation(generateTraceRequest(FRANCE_IP_ADDRESS));
    }

    private CountryData generateCountryDataResponse() {

        CountryData countryData = new CountryData();
        countryData.setCountryName("France");
        countryData.setCountryCode("FR");
        countryData.setCountryCode3("FRA");
        countryData.setCountryEmoji("fr");
        return countryData;
    }

    private CountryInformation generateCountryInformation() {

        CountryInformation countryInformation = new CountryInformation();
        countryInformation.setLanguages(new ArrayList<>());
        countryInformation.getLanguages().add(generateLanguage("fr", "French"));
        countryInformation.setCurrencies(new ArrayList<>());
        countryInformation.getCurrencies().add(generateCurrency("EUR"));
        countryInformation.setTimezones(Arrays.asList("UTC-10:00", "UTC-09:30", "UTC-09:00", "UTC-08:00"));
        countryInformation.setLatLng(new ArrayList<>());
        countryInformation.getLatLng().add(Long.valueOf(46));
        countryInformation.getLatLng().add(Long.valueOf(2));
        countryInformation.setAlpha3Code("FRA");
        return countryInformation;
    }

    private Language generateLanguage(String isoCode, String name) {

        Language language = new Language();
        language.setIsoCode(isoCode);
        language.setName(name);
        return language;
    }

    private Currency generateCurrency(String code) {

        Currency currency = new Currency();
        currency.setCode(code);
        return currency;
    }

    private TraceRequest generateTraceRequest(String ipAddress) {

        TraceRequest traceRequest = new TraceRequest();
        traceRequest.setIp(ipAddress);
        return traceRequest;
    }
}
