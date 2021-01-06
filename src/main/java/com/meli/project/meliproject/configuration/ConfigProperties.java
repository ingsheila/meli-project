package com.meli.project.meliproject.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "external.service.endpoint")
public class ConfigProperties {

    private String ipCountry;

    private String restCountries;

    private String exchange;

    private String apiKey;

    private String dataFixer;

    public String getIpCountry() {
        return ipCountry;
    }

    public void setIpCountry(String ipCountry) {
        this.ipCountry = ipCountry;
    }

    public String getRestCountries() {
        return restCountries;
    }

    public void setRestCountries(String restCountries) {
        this.restCountries = restCountries;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getDataFixer() {
        return dataFixer;
    }

    public void setDataFixer(String dataFixer) {
        this.dataFixer = dataFixer;
    }
}
