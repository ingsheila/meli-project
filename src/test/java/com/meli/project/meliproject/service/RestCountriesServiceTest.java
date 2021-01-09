package com.meli.project.meliproject.service;

import com.meli.project.meliproject.configuration.ConfigProperties;
import com.meli.project.meliproject.exception.RestCountriesServiceException;
import com.meli.project.meliproject.model.CountryInformation;
import com.meli.project.meliproject.model.Currency;
import com.meli.project.meliproject.model.Language;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(locations = "classpath:application-test.properties")
public class RestCountriesServiceTest {

    @Autowired
    private ConfigProperties configProperties;

    @MockBean
    private RestTemplate restTemplate;

    @MockBean
    private IFixerService fixerService;

    @Autowired
    private IRestCountriesService restCountriesService;

    private static final String FRANCE_COUNTRY = "France";
    private static final String USA_COUNTRY = "United States";

    @Test
    public void getCountryInformationByName() throws RestCountriesServiceException {

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                .fromHttpUrl(configProperties.getRestCountries().concat(FRANCE_COUNTRY));

        Mockito.when(restTemplate.exchange(Mockito.eq(uriComponentsBuilder.build().encode().toUri()),
                Mockito.eq(HttpMethod.GET),
                Mockito.any(),
                Mockito.eq(Object.class))).thenReturn(generateCountryInformation());

        CountryInformation countryInformation = this.restCountriesService.getCountryInformationByName(FRANCE_COUNTRY, "FR");

        assertEquals(46, countryInformation.getLatLng().get(0));
        assertEquals(2, countryInformation.getLatLng().get(1));
        assertEquals("French", countryInformation.getLanguages().get(0).getName());
        assertEquals("fr", countryInformation.getLanguages().get(0).getIsoCode());
        assertEquals("EUR", countryInformation.getCurrencies().get(0).getCode());
        assertEquals(4, countryInformation.getTimezones().size());
        assertEquals("UTC-10:00", countryInformation.getTimezones().get(0));
    }

    @Test
    public void getCountryInformationByNameAndCountryCode() throws RestCountriesServiceException {

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                .fromHttpUrl(configProperties.getRestCountries().concat(USA_COUNTRY));

        Mockito.when(restTemplate.exchange(Mockito.eq(uriComponentsBuilder.build().encode().toUri()),
                Mockito.eq(HttpMethod.GET),
                Mockito.any(),
                Mockito.eq(Object.class))).thenReturn(generateMultipleCountryInformation());

        CountryInformation countryInformation = this.restCountriesService.getCountryInformationByName(USA_COUNTRY, "USA");

        assertEquals(38, countryInformation.getLatLng().get(0));
        assertEquals(-97, countryInformation.getLatLng().get(1));
        assertEquals("English", countryInformation.getLanguages().get(0).getName());
        assertEquals("en", countryInformation.getLanguages().get(0).getIsoCode());
        assertEquals("USD", countryInformation.getCurrencies().get(0).getCode());
        assertEquals(3, countryInformation.getTimezones().size());
        assertEquals("UTC-11:00", countryInformation.getTimezones().get(1));
    }

    @Test(expected = RestCountriesServiceException.class)
    public void getCountryInformationException() throws RestCountriesServiceException {

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                .fromHttpUrl(configProperties.getRestCountries().concat(FRANCE_COUNTRY));

        Mockito.when(restTemplate.exchange(Mockito.eq(uriComponentsBuilder.build().encode().toUri()),
                Mockito.eq(HttpMethod.GET),
                Mockito.any(),
                Mockito.eq(Object.class))).thenReturn(generateErrorResponse());

        this.restCountriesService.getCountryInformationByName(FRANCE_COUNTRY, "FR");
    }

    @Test(expected = RestCountriesServiceException.class)
    public void getNullBodyResponse() throws RestCountriesServiceException {

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                .fromHttpUrl(configProperties.getRestCountries().concat(FRANCE_COUNTRY));

        Mockito.when(restTemplate.exchange(Mockito.eq(uriComponentsBuilder.build().encode().toUri()),
                Mockito.eq(HttpMethod.GET),
                Mockito.any(),
                Mockito.eq(Object.class))).thenReturn(generateNullBodyResponse());

        this.restCountriesService.getCountryInformationByName(FRANCE_COUNTRY, "FR");
    }

    @Test(expected = RestCountriesServiceException.class)
    public void getInvalidResponse() throws RestCountriesServiceException {

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                .fromHttpUrl(configProperties.getRestCountries().concat(FRANCE_COUNTRY));

        Mockito.when(restTemplate.exchange(Mockito.eq(uriComponentsBuilder.build().encode().toUri()),
                Mockito.eq(HttpMethod.GET),
                Mockito.any(),
                Mockito.eq(Object.class))).thenReturn(generateInvalidResponse());

        this.restCountriesService.getCountryInformationByName(FRANCE_COUNTRY, "FR");
    }

    private ResponseEntity<Object> generateCountryInformation() {

        ArrayList<Object> objectArrayList = new ArrayList<>();
        objectArrayList.add(generateCountryInformation("fr", "French", "EUR",
                Arrays.asList("UTC-10:00", "UTC-09:30", "UTC-09:00", "UTC-08:00"), Long.valueOf(46), Long.valueOf(2), "FRA"));
        return ResponseEntity.status(HttpStatus.OK).body(objectArrayList);
    }

    private ResponseEntity<Object> generateMultipleCountryInformation() {

        ArrayList<Object> objectArrayList = new ArrayList<>();
        objectArrayList.add(generateCountryInformation("en", "English", "USD",
                Arrays.asList("UTC-12:00", "UTC-11:00", "UTC-10:00"), Long.valueOf(38), Long.valueOf(-97), "USA"));
        objectArrayList.add(generateCountryInformation("en", "English", "USD",
                Arrays.asList("UTC-12:00"), null, null, "UMI"));
        return ResponseEntity.status(HttpStatus.OK).body(objectArrayList);
    }

    private CountryInformation generateCountryInformation(String isoCode, String language, String currency, List<String> timeZoneList,
                                                          Long latitude, Long longitude, String alpha3Code) {

        CountryInformation countryInformation = new CountryInformation();
        countryInformation.setLanguages(new ArrayList<>());
        countryInformation.getLanguages().add(generateLanguage(isoCode, language));
        countryInformation.setCurrencies(new ArrayList<>());
        countryInformation.getCurrencies().add(generateCurrency(currency));
        countryInformation.setTimezones(timeZoneList);
        countryInformation.setLatLng(new ArrayList<>());
        countryInformation.getLatLng().add(latitude);
        countryInformation.getLatLng().add(longitude);
        countryInformation.setAlpha3Code(alpha3Code);
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

    private ResponseEntity<Object> generateErrorResponse() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ArrayList<>());
    }

    private ResponseEntity<Object> generateNullBodyResponse() {
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    private ResponseEntity<Object> generateInvalidResponse() {
        return ResponseEntity.status(HttpStatus.OK).body(new HashMap<>());
    }

}
