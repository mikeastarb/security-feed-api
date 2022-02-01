package com.astarbia.securityapi.model.response;

import com.astarbia.securityapi.model.Incident;

import java.util.Arrays;
import java.util.List;

public class IncidentListResponse {
    private List<Incident> incidents = Arrays.asList();

    public List<Incident> getIncidents() {
        return incidents;
    }

    public int getTotalIncidents() {
        return incidents.size();
    }
}
