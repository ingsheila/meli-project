package com.meli.project.meliproject.controller;


import com.meli.project.api.model.Error;
import com.meli.project.api.model.*;
import com.meli.project.meliproject.constants.ConstantValues;
import com.meli.project.meliproject.exception.ContextualInformationException;
import com.meli.project.meliproject.service.IContextualInformationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping(ConstantValues.BASE_ENDPOINT)
@Api(value = "Contextual Information")
public class ContextualInformationController {

    private String operation;
    private String method;

    @Autowired
    private IContextualInformationService contextualInformationService;

    @ApiOperation(value = "Obtener informacion contextual de una invocacion realizada.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved message"),
            @ApiResponse(code = 400, message = "The server cannot or will not process the request due to an apparent client error."),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
    })
    @PostMapping(value = ConstantValues.TRACE_ENDPOINT, produces = "application/json; charset=UTF-8")
    public ResponseEntity<TraceResponse> getTraceInformation(@RequestBody(required = true) TraceRequest request) throws ContextualInformationException {
        setMethod(HttpMethod.POST.name());
        setOperation(ConstantValues.TRACE_ENDPOINT);
        return ResponseEntity.status(HttpStatus.OK).body(contextualInformationService.getTraceInformation(request));
    }

    @ApiOperation(value = "Obtener informacion estadistica sobre las distancias en relacion a BA de las invocaciones realizadas.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved message"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @GetMapping(value = ConstantValues.STATS_ENDPOINT, produces = "application/json; charset=UTF-8")
    public ResponseEntity<StatsResponse> getStatsInformation() throws ContextualInformationException {
        setMethod(HttpMethod.GET.name());
        setOperation(ConstantValues.STATS_ENDPOINT);
        return ResponseEntity.status(HttpStatus.OK).body(contextualInformationService.getStatsInformation());
    }

    @ExceptionHandler(ContextualInformationException.class)
    public ResponseEntity<TraceResponse> handleException(ContextualInformationException ex) {
        TraceResponse response = new TraceResponse();
        response.setErrors(new ArrayList<>());
        ex.getMessages().forEach(message -> {
            Error error = new Error();
            error.setCode(ex.getCode());
            error.setDetail(message);
            response.getErrors().add(error);
        });
        response.setMeta(new Meta());
        response.getMeta().setMethod(getMethod());
        response.getMeta().setOperation(ConstantValues.BASE_ENDPOINT.concat(getOperation()));

        return new ResponseEntity<>(response, ex.getStatus());
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
