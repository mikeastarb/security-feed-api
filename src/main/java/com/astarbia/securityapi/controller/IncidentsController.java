package com.astarbia.securityapi.controller;

import com.astarbia.securityapi.exception.AlreadyReportedException;
import com.astarbia.securityapi.exception.BadRequestException;
import com.astarbia.securityapi.exception.DuplicateValueException;
import com.astarbia.securityapi.exception.IncidentNotFoundException;
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
import java.util.Optional;
import java.util.stream.Collectors;

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

    @GetMapping(value = {"/incidents", "/incidents/{code}"}, produces = {"application/json"})
    public IncidentListResponse getAllIncidents(@PathVariable(name = "code") Optional<String> codeParam,
                                                @Valid @RequestParam(name = "size", required = false, defaultValue = "200") @Min(1) int sizeParam,
                                                @Valid @RequestParam(name = "page", required = false, defaultValue = "0") @Min(0) int pageParam) {
        log.info("Getting all incidents with user pagination and code filtering");
        sourceDataServices.refreshAllDataSources(); // TODO: Move this processing to a separate thread to keep API responsive

        int size = Math.min(sizeParam, 200);

        List<Incident> incidents = incidentRepo.getIncidents();

        if (codeParam.isPresent()) {
            incidents = incidents.stream().filter(incident -> incident.getSourceCode().equals(codeParam.get())).collect(Collectors.toList());
        }

        int startIndex = pageParam * size;
        if (startIndex > incidents.size()) {
            throw new BadRequestException("Page requested is out of range for maximum pages for that size");
        }

        IncidentListResponse incidentListResponse = new IncidentListResponse();
        List<Incident> pagedIncidents = incidents.subList(startIndex, Math.min(startIndex + size, incidents.size()));
        incidentListResponse.setIncidents(pagedIncidents);
        incidentListResponse.setPage(pageParam);
        incidentListResponse.setSize(size);
        incidentListResponse.setTotalIncidents(incidents.size());
        return incidentListResponse;
    }

    @GetMapping(value = "/incidents/{code}/{id}", produces = {"application/json"})
    public Incident getIncident(@PathVariable(name = "code") String code,
                                @PathVariable(name = "id") String id) {
        log.info("Getting an individual incident by code/ID pair");
        Optional<Incident> filterResult = incidentRepo.getIncidents().stream()
                .filter(incident -> incident.getSourceID().equals(id) && incident.getSourceCode().equals(code))
                .findAny();

        if(filterResult.isEmpty()) {
            throw new IncidentNotFoundException("Could not find the incident requested by the user");
        }

        return filterResult.get();
    }

    @PostMapping(value = "/incidents", produces = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public Incident addNewIncident(@Valid @RequestBody Incident incident) {
        log.info("Adding a new incident to the repository");
        try {
            return incidentRepo.addIncident(incident);
        } catch (DuplicateValueException e) {
            throw new AlreadyReportedException("An incident with the ID " + incident.getSourceID() + " already exists for " + incident.getSourceCode());
        }
    }
}
