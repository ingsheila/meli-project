package com.meli.project.meliproject.service.impl;

import com.meli.project.api.model.*;
import com.meli.project.meliproject.constants.ConstantValues;
import com.meli.project.meliproject.exception.ContextualInformationException;
import com.meli.project.meliproject.exception.CountryServiceException;
import com.meli.project.meliproject.exception.RestCountriesServiceException;
import com.meli.project.meliproject.mapper.CountryInformationMapper;
import com.meli.project.meliproject.model.Execution;
import com.meli.project.meliproject.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service("contextualInformationService")
public class ContextualInformationServiceImpl implements IContextualInformationService {

    private Logger logger;

    @PostConstruct
    public void init() {
        this.logger = LoggerFactory.getLogger(this.getClass());
    }

    @Autowired
    private IIPCountryService iPCountryService;

    @Autowired
    private IRestCountriesService restCountriesService;

    @Autowired
    private IFixerService fixerService;

    @Autowired
    private IMongoDBService mongoDBService;

    private CountryInformationMapper mapper = new CountryInformationMapper();

    @Override
    public TraceResponse getTraceInformation(TraceRequest request) throws ContextualInformationException {

        validateRequest(request);
        TraceResponse traceResponse = new TraceResponse();
        traceResponse.setMeta(addMeta(HttpMethod.POST.toString(), ConstantValues.TRACE_ENDPOINT));

        try {
            logger.info("MELI-PROJECT : Obteniendo el nombre del pais a partir de una IP. ");
            traceResponse.setData(mapper.convertToTraceData(request.getIp(), this.iPCountryService.getCountryDataByIP(request.getIp())));
        } catch (CountryServiceException exception) {
            throw new ContextualInformationException(ConstantValues.NOT_FOUND, exception.getMessages(),
                    HttpStatus.NOT_FOUND);
        }

        try {
            logger.info("MELI-PROJECT : Obteniendo informacion del pais a partir del nombre. ");
            mapper.convertToTraceDate(traceResponse.getData(),
                    this.restCountriesService.getCountryInformationByName(traceResponse.getData().getCountry()));
            traceResponse.getData().setCurrency(getEuroRate(traceResponse.getData().getCurrency()));
        } catch (RestCountriesServiceException exception) {
            throw new ContextualInformationException(ConstantValues.NOT_FOUND, exception.getMessages(),
                    HttpStatus.NOT_FOUND);
        }

        Executors.newSingleThreadExecutor().submit(() -> saveTraceInformation(traceResponse.getData()));
        return traceResponse;
    }

    @Override
    public StatsResponse getStatsInformation() throws ContextualInformationException {

        StatsResponse statsResponse = new StatsResponse();
        statsResponse.setMeta(addMeta(HttpMethod.GET.toString(), ConstantValues.STATS_ENDPOINT));
        StatsData statsData = new StatsData();

        logger.info("MELI-PROJECT : Obteniendo estadisticas. ");
        List<CompletableFuture<Execution>> futuresList = new ArrayList<>();

        futuresList.add(CompletableFuture.supplyAsync(() -> (getAverageExecutions(statsData))));
        futuresList.add(CompletableFuture.supplyAsync(() -> (getClosestDistance(statsData))));
        futuresList.add(CompletableFuture.supplyAsync(() -> (getFarthestDistance(statsData))));

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futuresList.toArray(new CompletableFuture[futuresList.size()]));
        CompletableFuture<List<Execution>> allCompletableFuture = allFutures.thenApply(future ->
                futuresList.stream().map(CompletableFuture::join).collect(Collectors.toList()));

        try {
            allCompletableFuture.get();
        } catch (InterruptedException | ExecutionException exception) {
            logger.error("Ha ocurrido un error al consultar las estadisticas. ");
            Thread.currentThread().interrupt();
            throw new ContextualInformationException(ConstantValues.INTERNAL_SERVER_ERROR_CODE,
                    Arrays.asList(ConstantValues.INTERNAL_SERVER_ERROR_CODE), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        statsResponse.setData(statsData);
        return statsResponse;
    }

    private Execution getAverageExecutions(StatsData statsData) {

        logger.info("MELI-PROJECT : Obteniendo distancia promedio. ");
        Execution execution = this.mongoDBService.getAverageExecutions();
        statsData.setAverageDistance(String.valueOf(Math.round(execution.getTotal() * 100d) / 100d).concat(ConstantValues.KMS_STRING));
        return execution;
    }

    private Execution getClosestDistance(StatsData statsData) {

        logger.info("MELI-PROJECT : Obteniendo distancia mas cercana. ");
        Execution closestExecution = this.mongoDBService.getClosestDistance();
        statsData.setClosestDistance(new DistanceDetails());
        statsData.getClosestDistance().setCountry(closestExecution.getCountry());
        statsData.getClosestDistance().setDistance(String.valueOf(
                closestExecution.getDistance()).concat(ConstantValues.KMS_STRING));
        return closestExecution;
    }

    private Execution getFarthestDistance(StatsData statsData) {

        logger.info("MELI-PROJECT : Obteniendo distancia mas lejana. ");
        Execution farthestExecution = this.mongoDBService.getFarthestDistance();
        statsData.setFarthestDistance(new DistanceDetails());
        statsData.getFarthestDistance().setDistance(String.valueOf(farthestExecution.getDistance()).concat(ConstantValues.KMS_STRING));
        statsData.getFarthestDistance().setCountry(farthestExecution.getCountry());
        return farthestExecution;
    }

    private void validateRequest(TraceRequest request) throws ContextualInformationException {

        Pattern pattern = Pattern.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
        Matcher matcher = pattern.matcher(request.getIp());
        if (!matcher.matches()) {
            throw new ContextualInformationException(ConstantValues.BAD_REQUEST_CODE,
                    Arrays.asList("El valor del campo IP no es valido."), HttpStatus.BAD_REQUEST);
        }
    }

    private String getEuroRate(String currency) {

        Long rate = this.fixerService.getRate(currency);
        return rate != null ? currency + " (1 EUR = " + rate + " " + currency + ")" : currency;
    }

    private void saveTraceInformation(TraceData traceData) {

        Execution execution = new Execution();
        execution.setDistance(Double.valueOf(traceData.getEstimatedDistance().replace(ConstantValues.KMS_STRING, "")));
        execution.setCountry(traceData.getCountry());
        this.mongoDBService.saveOrUpdateExecution(execution);
    }

    private Meta addMeta(String method, String operation) {

        Meta meta = new Meta();
        meta.setMethod(method);
        meta.setOperation(ConstantValues.BASE_ENDPOINT.concat(operation));
        return meta;
    }
}
