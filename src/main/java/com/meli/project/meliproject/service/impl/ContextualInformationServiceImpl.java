package com.meli.project.meliproject.service.impl;

import com.meli.project.api.model.Meta;
import com.meli.project.api.model.StatsResponse;
import com.meli.project.api.model.TraceRequest;
import com.meli.project.api.model.TraceResponse;
import com.meli.project.meliproject.constants.ConstantValues;
import com.meli.project.meliproject.exception.ContextualInformationException;
import com.meli.project.meliproject.exception.CountryServiceException;
import com.meli.project.meliproject.exception.RestCountriesServiceException;
import com.meli.project.meliproject.mapper.CountryInformationMapper;
import com.meli.project.meliproject.service.IContextualInformationService;
import com.meli.project.meliproject.service.IFixerService;
import com.meli.project.meliproject.service.IIPCountryService;
import com.meli.project.meliproject.service.IRestCountriesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        return traceResponse;
    }

    @Override
    public StatsResponse getStatsInformation() throws ContextualInformationException {

        StatsResponse statsResponse = new StatsResponse();
        statsResponse.setMeta(addMeta(HttpMethod.GET.toString(), ConstantValues.STATS_ENDPOINT));

        logger.info("MELI-PROJECT : Obteniendo estadisticas. ");

        return statsResponse;
    }

    private void validateRequest(TraceRequest request) throws ContextualInformationException {

        Pattern pattern = Pattern.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
        Matcher matcher = pattern.matcher(request.getIp());
        if(!matcher.matches()){
            throw new ContextualInformationException(ConstantValues.BAD_REQUEST_CODE,
                    Arrays.asList("El valor del campo IP no es valido."), HttpStatus.BAD_REQUEST);
        }
    }

    private String getEuroRate(String currency) {

        Long rate = this.fixerService.getRate(currency);
        return rate != null ? currency + " (1 EUR = " + rate + " " + currency + ")" : currency;
    }

    private Meta addMeta(String method, String operation) {

        Meta meta = new Meta();
        meta.setMethod(method);
        meta.setOperation(ConstantValues.BASE_ENDPOINT.concat(operation));
        return meta;
    }
}
