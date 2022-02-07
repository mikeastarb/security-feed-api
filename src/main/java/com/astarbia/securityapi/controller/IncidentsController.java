package com.astarbia.securityapi.controller;

import com.astarbia.securityapi.exception.DuplicateValueException;
import com.astarbia.securityapi.model.Incident;
import com.astarbia.securityapi.model.response.IncidentListResponse;
import com.astarbia.securityapi.repo.IncidentRepo;
import com.astarbia.securityapi.service.SourceDataServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class IncidentsController {
    private static final Logger logger = LoggerFactory.getLogger(IncidentsController.class);

    private final IncidentRepo incidentRepo;
    private final SourceDataServices sourceDataServices;

    public IncidentsController(IncidentRepo incidentRepo, SourceDataServices sourceDataServices) {
        this.incidentRepo = incidentRepo;
        this.sourceDataServices = sourceDataServices;
    }

    @GetMapping(value = "/incidents", produces = {"application/json"})
    public ResponseEntity getAllIncidents(@RequestParam(name="size", required=false, defaultValue="200") String sizeParam,
                                          @RequestParam(name="page", required=false, defaultValue="0") String pageParam) {
        int size;
        int page;
        try {
            size = Integer.parseInt(sizeParam);
            page = Integer.parseInt(pageParam);
        } catch (NumberFormatException e) {
            logger.warn("User tried to pass a bad integer", e);
            return new ResponseEntity<>("Size and Page must be integers", HttpStatus.BAD_REQUEST);
        }

        if(size < 1) {
            return new ResponseEntity<>("Size of page must be a positive number", HttpStatus.BAD_REQUEST);
        }

        if(page < 0) {
            return new ResponseEntity<>("Page number must be zero or greater", HttpStatus.BAD_REQUEST);
        }

        sourceDataServices.refreshAllDataSources(); // TODO: Move this processing to a separate thread to keep API responsive

        if(page * size > incidentRepo.getIncidents().size()) {
            return new ResponseEntity<>("Page requested is out of range for maximum pages for that size", HttpStatus.BAD_REQUEST);
        }

        return getAllIncidents(Math.min(size, 200), page);
    }

    private ResponseEntity<IncidentListResponse> getAllIncidents(int size, int page) {
        IncidentListResponse incidentListResponse = new IncidentListResponse();
        List<Incident> pagedIncidents = incidentRepo.getIncidents().subList(page * size, Math.min((page * size) + size, incidentRepo.getIncidents().size()));
        incidentListResponse.setIncidents(pagedIncidents);
        incidentListResponse.setPage(page);
        incidentListResponse.setSize(size);
        incidentListResponse.setTotalIncidents(incidentRepo.getIncidents().size());
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
