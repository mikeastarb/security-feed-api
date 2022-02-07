package com.astarbia.securityapi.model.response;

import com.astarbia.securityapi.model.Incident;

import java.util.Collections;
import java.util.List;

public class IncidentListResponse {
    private List<Incident> incidents = Collections.emptyList();

    public List<Incident> getIncidents() {
        return incidents;
    }

    public void setIncidents(List<Incident> incidents) {
        this.incidents = incidents;
    }

    public int getTotalIncidents() {
        return incidents.size();
    }
}
