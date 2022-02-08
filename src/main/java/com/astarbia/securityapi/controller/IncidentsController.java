package com.astarbia.securityapi.controller;

import com.astarbia.securityapi.exception.AlreadyReportedException;
import com.astarbia.securityapi.exception.BadRequestException;
import com.astarbia.securityapi.exception.DuplicateValueException;
import com.astarbia.securityapi.model.Incident;
import com.astarbia.securityapi.model.response.IncidentListResponse;
import com.astarbia.securityapi.repo.IncidentRepo;
import com.astarbia.securityapi.service.SourceDataServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@Validated
@Slf4j
public class IncidentsController {
    private final IncidentRepo incidentRepo;
    private final SourceDataServices sourceDataServices;

    public IncidentsController(IncidentRepo incidentRepo, SourceDataServices sourceDataServices) {
        this.incidentRepo = incidentRepo;
        this.sourceDataServices = sourceDataServices;
    }

    @GetMapping(value = "/incidents", produces = {"application/json"})
    public IncidentListResponse getAllIncidents(@Valid @RequestParam(name = "size", required = false, defaultValue = "200") @Min(1) int sizeParam,
                                                @Valid @RequestParam(name = "page", required = false, defaultValue = "0") @Min(0) int pageParam) {
        sourceDataServices.refreshAllDataSources(); // TODO: Move this processing to a separate thread to keep API responsive

        int size = Math.min(sizeParam, 200);

        if (pageParam * size > incidentRepo.getIncidents().size()) {
            throw new BadRequestException("Page requested is out of range for maximum pages for that size");
        }

        IncidentListResponse incidentListResponse = new IncidentListResponse();
        List<Incident> pagedIncidents = incidentRepo.getIncidents().subList(pageParam * size, Math.min((pageParam * size) + size, incidentRepo.getIncidents().size()));
        incidentListResponse.setIncidents(pagedIncidents);
        incidentListResponse.setPage(pageParam);
        incidentListResponse.setSize(size);
        incidentListResponse.setTotalIncidents(incidentRepo.getIncidents().size());
        return incidentListResponse;
    }

    @PostMapping(value = "/incidents", produces = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public Incident addNewIncident(@Valid @RequestBody Incident incident) {
        try {
            return incidentRepo.addIncident(incident);
        } catch (DuplicateValueException e) {
            throw new AlreadyReportedException("An incident with the ID " + incident.getSourceID() + " already exists for " + incident.getSourceCode());
        }
    }
}
