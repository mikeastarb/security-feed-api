package com.astarbia.securityapi.controller;

import com.astarbia.securityapi.exception.DuplicateValueException;
import com.astarbia.securityapi.model.Incident;
import com.astarbia.securityapi.model.response.IncidentListResponse;
import com.astarbia.securityapi.repo.IncidentRepo;
import com.astarbia.securityapi.service.SourceDataServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IncidentsController {

    private final IncidentRepo incidentRepo;
    private final SourceDataServices sourceDataServices;

    public IncidentsController(IncidentRepo incidentRepo, SourceDataServices sourceDataServices) {
        this.incidentRepo = incidentRepo;
        this.sourceDataServices = sourceDataServices;
    }

    @GetMapping(value = "/incidents", produces = {"application/json"})
    public ResponseEntity getAllIncidents() {
        sourceDataServices.refreshAllDataSources(); // TODO: Move this processing to a separate thread to keep API responsive
        IncidentListResponse incidentListResponse = new IncidentListResponse();
        incidentListResponse.setIncidents(incidentRepo.getIncidents());
        return new ResponseEntity<>(incidentListResponse, HttpStatus.OK);
    }

    @PostMapping(value = "/incidents", produces = {"application/json"})
    public ResponseEntity addNewIncident(@RequestBody Incident incident) {
        if (!incident.isValid()) {
            return new ResponseEntity<>("A required field was missing from the request body for a new incident", HttpStatus.BAD_REQUEST);
        }
        try {
            return new ResponseEntity<>(incidentRepo.addIncident(incident), HttpStatus.CREATED);
        } catch (DuplicateValueException e) {
            return new ResponseEntity<>("An incident with the ID " + incident.getSourceID() + " already exists for " + incident.getSourceCode(), HttpStatus.ALREADY_REPORTED);
        }
    }
}
