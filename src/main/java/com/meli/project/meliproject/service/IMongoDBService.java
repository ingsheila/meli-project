package com.meli.project.meliproject.service;

import com.meli.project.meliproject.model.Execution;

public interface IMongoDBService {

    /**
     * Permite obtener el promedio de las ejecuciones del servicio
     *
     * @return
     */
    Execution getAverageExecutions();

    /**
     * Permite almacenar o actualizar una ejecucion del servicio
     */
    void saveOrUpdateExecution(Execution request);

    /**
     * Permite obtener la execution con la distancia mas cercana
     *
     * @return
     */
    Execution getClosestDistance();

    /**
     * Permite obtener la execution con la distancia mas lejana
     *
     * @return
     */
    Execution getFarthestDistance();
}
