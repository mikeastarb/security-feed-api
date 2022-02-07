package com.astarbia.securityapi.repo;

import com.astarbia.securityapi.exception.DuplicateValueException;
import com.astarbia.securityapi.model.Incident;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class IncidentRepo {
    private final List<Incident> incidents = new ArrayList<>();

    public Incident addIncident(Incident incident) throws DuplicateValueException {
        if (incidents.stream().anyMatch(existing -> existing.getSourceID().equals(incident.getSourceID()) && existing.getSourceCode().equals(incident.getSourceCode()))) {
            throw new DuplicateValueException();
        }

        incidents.add(incident);
        return incident;
    }

    public List<Incident> getIncidents() {
        return incidents;
    }
}
