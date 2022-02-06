package com.astarbia.securityapi.controller;

import com.astarbia.securityapi.model.response.IncidentListResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IncidentsController {

    @GetMapping(value = "/incidents", produces= { "application/json" })
    public IncidentListResponse getAllIncidents() {
        return new IncidentListResponse();
    }

    @PostMapping(value = "/incidents", produces = { "application/json" })
    public void addNewIncident() {

    }
}
