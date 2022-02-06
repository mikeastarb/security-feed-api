package com.astarbia.securityapi.controller;

import com.astarbia.securityapi.model.Incident;
import com.astarbia.securityapi.model.response.IncidentListResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IncidentsController {
    @GetMapping(value = "/incidents", produces= { "application/json" })
    public IncidentListResponse getAllIncidents() {
        return new IncidentListResponse();
    }

    @PostMapping(value = "/incidents", produces = { "application/json" })
    public Incident addNewIncident(@RequestBody Incident incident) {
        return incident;
    }
}
