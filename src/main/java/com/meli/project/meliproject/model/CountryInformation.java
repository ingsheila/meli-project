package com.meli.project.meliproject.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CountryInformation {

    @JsonProperty(value = "name")
    private String name;

    @JsonProperty(value = "alpha2Code")
    private String alpha2Code;

    @JsonProperty(value = "alpha3Code")
    private String alpha3Code;

    @JsonProperty(value = "capital")
    private String capital;

    @JsonProperty(value = "altSpellings")
    private List<String> altSpellings;

    @JsonProperty(value = "latlng")
    private List<Long> latLng;

    @JsonProperty(value = "timezones")
    private List<String> timezones;

    @JsonProperty(value = "currencies")
    private List<Currency> currencies;

    @JsonProperty(value = "languages")
    private List<Language> languages;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlpha2Code() {
        return alpha2Code;
    }

    public void setAlpha2Code(String alpha2Code) {
        this.alpha2Code = alpha2Code;
    }

    public String getAlpha3Code() {
        return alpha3Code;
    }

    public void setAlpha3Code(String alpha3Code) {
        this.alpha3Code = alpha3Code;
    }

    public String getCapital() {
        return capital;
    }

    public void setCapital(String capital) {
        this.capital = capital;
    }

    public List<String> getAltSpellings() {
        return altSpellings;
    }

    public void setAltSpellings(List<String> altSpellings) {
        this.altSpellings = altSpellings;
    }

    public List<Long> getLatLng() {
        return latLng;
    }

    public void setLatLng(List<Long> latLng) {
        this.latLng = latLng;
    }

    public List<String> getTimezones() {
        return timezones;
    }

    public void setTimezones(List<String> timezones) {
        this.timezones = timezones;
    }

    public List<Currency> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(List<Currency> currencies) {
        this.currencies = currencies;
    }

    public List<Language> getLanguages() {
        return languages;
    }

    public void setLanguages(List<Language> languages) {
        this.languages = languages;
    }
}
