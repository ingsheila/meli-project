package com.meli.project.meliproject.mapper;

import com.meli.project.api.model.TraceData;
import com.meli.project.meliproject.model.CountryData;
import com.meli.project.meliproject.model.CountryInformation;
import com.meli.project.meliproject.model.Currency;
import com.meli.project.meliproject.model.Language;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class CountryInformationMapperTest {

    private CountryInformationMapper mapper = new CountryInformationMapper();

    private static final String FRANCE_IP = "90.6.7.8";

    @Test
    public void convertToTraceData() {

        TraceData traceData = this.mapper.convertToTraceData(FRANCE_IP, generateCountryData());
        assertEquals("France", traceData.getCountry());
        assertEquals(FRANCE_IP, traceData.getIp());

        this.mapper.convertToTraceData(traceData,generateCountryInformation());
        assertEquals("fr", traceData.getIsoCode());
        assertEquals("French (fr)", traceData.getLanguages().get(0));
        assertEquals(4, traceData.getTimes().size());
        assertEquals("UTC-10:00", traceData.getTimes().get(0));
        assertEquals("EUR", traceData.getCurrency());
        assertEquals("10823.68 kms", traceData.getEstimatedDistance());
    }

    @Test
    public void convertToTraceDataWithMissingInfo() {

        TraceData traceData = this.mapper.convertToTraceData(FRANCE_IP, generateCountryData());
        assertEquals("France", traceData.getCountry());
        assertEquals(FRANCE_IP, traceData.getIp());

        this.mapper.convertToTraceData(traceData, generateEmptyCountryInformation());
        assertNull(traceData.getIsoCode());
        assertNull(traceData.getLanguages());
        assertNull(traceData.getTimes());
        assertNull(traceData.getCurrency());
        assertNull(traceData.getEstimatedDistance());
    }

    private CountryData generateCountryData() {

        CountryData countryData = new CountryData();
        countryData.setCountryName("France");
        return countryData;
    }

    private CountryInformation generateCountryInformation(){

        CountryInformation countryInformation = new CountryInformation();
        countryInformation.setLanguages(new ArrayList<>());
        countryInformation.getLanguages().add(generateLanguage("fr","French"));
        countryInformation.setCurrencies(new ArrayList<>());
        countryInformation.getCurrencies().add(generateCurrency("EUR"));
        countryInformation.setTimezones(Arrays.asList("UTC-10:00","UTC-09:30","UTC-09:00","UTC-08:00"));
        countryInformation.setLatLng(new ArrayList<>());
        countryInformation.getLatLng().add(Long.valueOf(46));
        countryInformation.getLatLng().add(Long.valueOf(2));
        return countryInformation;
    }

    private Language generateLanguage(String isoCode, String name){

        Language language = new Language();
        language.setIsoCode(isoCode);
        language.setName(name);
        return language;
    }

    private Currency generateCurrency(String code){

        Currency currency = new Currency();
        currency.setCode(code);
        return currency;
    }

    private CountryInformation generateEmptyCountryInformation(){

        CountryInformation countryInformation = new CountryInformation();
        countryInformation.setLanguages(new ArrayList<>());
        countryInformation.setCurrencies(new ArrayList<>());
        countryInformation.setTimezones(null);
        countryInformation.setLatLng(new ArrayList<>());
        return countryInformation;
    }
}
