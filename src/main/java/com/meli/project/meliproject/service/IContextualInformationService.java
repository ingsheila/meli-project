package com.meli.project.meliproject.service;

import com.meli.project.api.model.StatsResponse;
import com.meli.project.api.model.TraceRequest;
import com.meli.project.api.model.TraceResponse;
import com.meli.project.meliproject.exception.ContextualInformationException;

public interface IContextualInformationService {

    /**
     * Permite obtener informacion contextual de una invocacion realizada.
     *
     * @param request
     * @return TraceResponse
     * @throws ContextualInformationException
     */
    TraceResponse getTraceInformation(TraceRequest request) throws ContextualInformationException;

    /**
     * Permite obtener informacion estadistica sobre las distancias en relacion a BA de las invocaciones realizadas.
     *
     * @return StatsResponse: Distancia mas cercana, mas lejana y el promedio general
     */
    StatsResponse getStatsInformation() throws ContextualInformationException;
}
