package com.meli.project.meliproject.controller;

import com.meli.project.api.model.*;
import com.meli.project.meliproject.AbstractIntegrationTest;
import com.meli.project.meliproject.constants.ConstantValues;
import com.meli.project.meliproject.exception.ContextualInformationException;
import com.meli.project.meliproject.service.IContextualInformationService;
import com.meli.project.meliproject.service.IFixerService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

public class ContextualInformationControllerTest extends AbstractIntegrationTest {

    @MockBean
    private IFixerService fixerService;

    @MockBean
    private IContextualInformationService contextualInformationService;

    private String jsonPathData = "$.data";
    private String jsonPathError = "$.errors";
    private String jsonPathMeta = "$.meta";
    private String indexZero = "[0]";

    private String jsonPathMethod = ".method";
    private String jsonPathOperation = ".operation";
    private String jsonPathCode = ".code";
    private String jsonPathDetail = ".detail";

    private static final String GERMANY_IP = "5.6.7.8";

    @Autowired
    private WebApplicationContext wac;

    @Before
    public void setUp() {
        mockMvc = webAppContextSetup(wac).build();
    }

    @Test
    public void getTrace() throws Exception {

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(
                ConstantValues.BASE_ENDPOINT.concat(ConstantValues.TRACE_ENDPOINT));
        Mockito.when(contextualInformationService.getTraceInformation(any())).thenReturn(generateTraceResponse());

        String json = objectToJson(generateBody());
        mockMvc.perform(request
                .contentType(contentType)
                .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath(jsonPathData, notNullValue()))
                .andExpect(jsonPath(jsonPathData + ".ip", is(GERMANY_IP)))
                .andExpect(jsonPath(jsonPathData + ".date", is("2021-01-07 23:55:01")))
                .andExpect(jsonPath(jsonPathData + ".country", is("Germany")))
                .andExpect(jsonPath(jsonPathData + ".iso_code", is("de")))
                .andExpect(jsonPath(jsonPathData + ".languages" + indexZero, is("German (de)")))
                .andExpect(jsonPath(jsonPathData + ".currency", is("EUR")))
                .andExpect(jsonPath(jsonPathData + ".times" + indexZero, is("UTC+01:00")))
                .andExpect(jsonPath(jsonPathData + ".estimated_distance", is("11578.14 kms")))
                .andExpect(jsonPath(jsonPathMeta, notNullValue()))
                .andExpect(jsonPath(jsonPathMeta + jsonPathMethod, is("POST")))
                .andExpect(jsonPath(jsonPathMeta + jsonPathOperation, is(ConstantValues.BASE_ENDPOINT.concat(ConstantValues.TRACE_ENDPOINT))));
    }

    @Test
    public void getTraceErrorResponse() throws Exception {

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(
                ConstantValues.BASE_ENDPOINT.concat(ConstantValues.TRACE_ENDPOINT));
        Mockito.when(contextualInformationService.getTraceInformation(any()))
                .thenThrow(new ContextualInformationException(ConstantValues.NOT_FOUND,
                        Arrays.asList("No se encontraron resultados. "), HttpStatus.NOT_FOUND));

        String json = objectToJson(generateBody());
        mockMvc.perform(request
                .contentType(contentType)
                .content(json))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath(jsonPathError + indexZero + jsonPathDetail, is("No se encontraron resultados. ")))
                .andExpect(jsonPath(jsonPathError + indexZero + jsonPathCode, is("404")));
    }

    @Test
    public void getStats() throws Exception {

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(
                ConstantValues.BASE_ENDPOINT.concat(ConstantValues.STATS_ENDPOINT));
        Mockito.when(contextualInformationService.getStatsInformation()).thenReturn(generateStatsResponse());

        mockMvc.perform(request
                .contentType(contentType))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath(jsonPathData, notNullValue()))
                .andExpect(jsonPath(jsonPathData + ".average_distance", is("5432.87 kms")))
                .andExpect(jsonPath(jsonPathData + ".closest_distance" + ".country", is("Argentina")))
                .andExpect(jsonPath(jsonPathData + ".closest_distance" + ".distance", is("521.93 kms")))
                .andExpect(jsonPath(jsonPathData + ".farthest_distance" + ".country", is("China")))
                .andExpect(jsonPath(jsonPathData + ".farthest_distance" + ".distance", is("18518.28 kms")))
                .andExpect(jsonPath(jsonPathMeta, notNullValue()))
                .andExpect(jsonPath(jsonPathMeta + jsonPathMethod, is("GET")))
                .andExpect(jsonPath(jsonPathMeta + jsonPathOperation, is(ConstantValues.BASE_ENDPOINT.concat(ConstantValues.STATS_ENDPOINT))));
    }

    @Test
    public void getStatsErrorResponse() throws Exception {

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(
                ConstantValues.BASE_ENDPOINT.concat(ConstantValues.STATS_ENDPOINT));
        Mockito.when(contextualInformationService.getStatsInformation())
                .thenThrow(new ContextualInformationException(ConstantValues.INTERNAL_SERVER_ERROR_CODE,
                        Arrays.asList("Error interno del servidor."), HttpStatus.INTERNAL_SERVER_ERROR));

        String json = objectToJson(generateBody());
        mockMvc.perform(request
                .contentType(contentType)
                .content(json))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath(jsonPathError + indexZero + jsonPathDetail, is("Error interno del servidor.")))
                .andExpect(jsonPath(jsonPathError + indexZero + jsonPathCode, is("500")));
    }


    private TraceResponse generateTraceResponse() {

        TraceResponse traceResponse = new TraceResponse();
        traceResponse.setData(generateTraceData());
        traceResponse.setMeta(generateMeta(HttpMethod.POST.name(), ConstantValues.TRACE_ENDPOINT));
        return traceResponse;
    }

    private TraceData generateTraceData() {

        TraceData traceData = new TraceData();
        traceData.setIp(GERMANY_IP);
        traceData.setCurrency("EUR");
        traceData.setCountry("Germany");
        traceData.setDate("2021-01-07 23:55:01");
        traceData.setIsoCode("de");
        traceData.setLanguages(Arrays.asList("German (de)"));
        traceData.setTimes(Arrays.asList("UTC+01:00"));
        traceData.setEstimatedDistance("11578.14 kms");
        return traceData;
    }

    private Meta generateMeta(String method, String operation) {

        Meta meta = new Meta();
        meta.setMethod(method);
        meta.setOperation(ConstantValues.BASE_ENDPOINT.concat(operation));
        return meta;
    }

    private TraceRequest generateBody() {

        TraceRequest traceRequest = new TraceRequest();
        traceRequest.setIp(GERMANY_IP);
        return traceRequest;
    }

    private StatsResponse generateStatsResponse() {

        StatsResponse statsResponse = new StatsResponse();
        statsResponse.setData(generateStatsData());
        statsResponse.setMeta(generateMeta(HttpMethod.GET.name(), ConstantValues.STATS_ENDPOINT));
        return statsResponse;
    }

    private StatsData generateStatsData() {

        StatsData statsData = new StatsData();
        statsData.setClosestDistance(new DistanceDetails());
        statsData.getClosestDistance().setCountry("Argentina");
        statsData.getClosestDistance().setDistance("521.93 kms");

        statsData.setFarthestDistance(new DistanceDetails());
        statsData.getFarthestDistance().setDistance("18518.28 kms");
        statsData.getFarthestDistance().setCountry("China");

        statsData.setAverageDistance("5432.87 kms");
        return statsData;
    }

}
