package com.astarbia.securityapi.controller;

import com.astarbia.securityapi.exception.AlreadyReportedException;
import com.astarbia.securityapi.exception.BadRequestException;
import com.astarbia.securityapi.exception.DuplicateValueException;
import com.astarbia.securityapi.model.Incident;
import com.astarbia.securityapi.model.response.IncidentListResponse;
import com.astarbia.securityapi.repo.IncidentRepo;
import com.astarbia.securityapi.service.SourceDataServices;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

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
    public IncidentListResponse getAllIncidents(@RequestParam(name="size", required=false, defaultValue="200") String sizeParam,
                                          @RequestParam(name="page", required=false, defaultValue="0") String pageParam) {
        int size;
        int page;
        try {
            size = Integer.parseInt(sizeParam);
            page = Integer.parseInt(pageParam);
        } catch (NumberFormatException e) {
            logger.warn("User tried to pass a bad integer", e);
            throw new BadRequestException("Size and Page must be integers");
        }

        if(size < 1) {
            logger.warn("User tried to get a count less than 1");
            throw new BadRequestException("Size of page must be a positive number");
        }

        if(page < 0) {
            logger.warn("User tried to get a page count less than 0");
            throw new BadRequestException("Page number must be zero or greater");
        }

        sourceDataServices.refreshAllDataSources(); // TODO: Move this processing to a separate thread to keep API responsive

        if(page * size > incidentRepo.getIncidents().size()) {
            throw new BadRequestException("Page requested is out of range for maximum pages for that size");
        }

        return getAllIncidents(Math.min(size, 200), page);
    }

    private IncidentListResponse getAllIncidents(int size, int page) {
        IncidentListResponse incidentListResponse = new IncidentListResponse();
        List<Incident> pagedIncidents = incidentRepo.getIncidents().subList(page * size, Math.min((page * size) + size, incidentRepo.getIncidents().size()));
        incidentListResponse.setIncidents(pagedIncidents);
        incidentListResponse.setPage(page);
        incidentListResponse.setSize(size);
        incidentListResponse.setTotalIncidents(incidentRepo.getIncidents().size());
        return incidentListResponse;
    }

    @PostMapping(value = "/incidents", produces = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public Incident addNewIncident(@RequestBody Incident incident) {
        if (!incident.isValid()) {
            throw new BadRequestException("A required field was missing from the request body for a new incident");
        }
        try {
            return incidentRepo.addIncident(incident);
        } catch (DuplicateValueException e) {
            throw new AlreadyReportedException("An incident with the ID " + incident.getSourceID() + " already exists for " + incident.getSourceCode());
        }
    }
}
