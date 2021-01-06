package com.meli.project.meliproject.service;

import com.meli.project.meliproject.exception.RestCountriesServiceException;
import com.meli.project.meliproject.model.CountryInformation;

public interface IRestCountriesService {

    /**
     * Permite obtener informacion detallada correspondiente a un pais
     *
     * @param name
     * @return
     * @throws RestCountriesServiceException
     */
    CountryInformation getCountryInformationByName(String name) throws RestCountriesServiceException;
}
