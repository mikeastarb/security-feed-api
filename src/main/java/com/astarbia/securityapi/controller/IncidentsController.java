package com.astarbia.securityapi.controller;

import com.astarbia.securityapi.exception.DuplicateValueException;
import com.astarbia.securityapi.model.Incident;
import com.astarbia.securityapi.model.response.IncidentListResponse;
import com.astarbia.securityapi.repo.IncidentRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IncidentsController {

    private final IncidentRepo incidentRepo;

    public IncidentsController(IncidentRepo incidentRepo) {
        this.incidentRepo = incidentRepo;
    }

    @GetMapping(value = "/incidents", produces= { "application/json" })
    public IncidentListResponse getAllIncidents() {
        return new IncidentListResponse();
    }

    @PostMapping(value = "/incidents", produces = { "application/json" })
    public Incident addNewIncident(@RequestBody Incident incident) throws DuplicateValueException {
        return incidentRepo.addIncident(incident);
    }
}
