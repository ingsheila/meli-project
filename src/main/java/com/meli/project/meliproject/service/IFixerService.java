package com.meli.project.meliproject.service;

public interface IFixerService {

    /**
     * Permite obtener el tipo de cambio más recientes por moneda con respecto al euro
     *
     * @return
     */
    Double getRate(String currency);
}
