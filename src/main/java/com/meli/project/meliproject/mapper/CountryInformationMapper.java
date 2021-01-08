package com.meli.project.meliproject.mapper;

import com.meli.project.api.model.TraceData;
import com.meli.project.meliproject.constants.ConstantValues;
import com.meli.project.meliproject.model.CountryData;
import com.meli.project.meliproject.model.CountryInformation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class CountryInformationMapper {

    public TraceData convertToTraceData(String ipAddress, CountryData countryData) {

        TraceData traceData = new TraceData();
        traceData.setIp(ipAddress);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        traceData.setDate(LocalDateTime.now().format(formatter));
        traceData.setCountry(countryData.getCountryName());
        return traceData;
    }

    public void convertToTraceDate(TraceData traceData, CountryInformation countryInformation) {

        traceData.setIsoCode(countryInformation.getLanguages().get(0).getIsoCode());
        traceData.setLanguages(new ArrayList<>());
        countryInformation.getLanguages().forEach(language ->
                traceData.getLanguages().add(language.getName().concat(" (" + language.getIsoCode() + ")")));
        traceData.setTimes(countryInformation.getTimezones());
        traceData.setCurrency(countryInformation.getCurrencies().get(0).getCode());
        traceData.setEstimatedDistance(calculateDistance(countryInformation.getLatLng().get(0), countryInformation.getLatLng().get(1)));
    }

    private String calculateDistance(double lat1, double lng1) {

        double earthRadius = 6378;
        double dLat = Math.toRadians(ConstantValues.LATITUDE_BA - lat1);
        double dLng = Math.toRadians(ConstantValues.LONGITUDE_BA - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(ConstantValues.LATITUDE_BA)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return String.valueOf(Math.round(earthRadius * c * 100d) / 100d).concat(" kms");
    }

}
