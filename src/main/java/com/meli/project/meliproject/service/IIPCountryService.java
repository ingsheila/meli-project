package com.meli.project.meliproject.service;

import com.meli.project.meliproject.exception.CountryServiceException;
import com.meli.project.meliproject.model.CountryData;

public interface IIPCountryService {

    /**
     * Permite obtener el pais correspondiente a una IP dada.
     *
     * @param ipAddress
     * @return
     * @throws CountryServiceException
     */
    CountryData getCountryDataByIP(String ipAddress) throws CountryServiceException;
}
