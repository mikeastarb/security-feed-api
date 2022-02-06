package com.astarbia.securityapi.model.response;

import com.astarbia.securityapi.model.Incident;

import java.util.Arrays;
import java.util.List;

public class IncidentListResponse {
    private List<Incident> incidents = Arrays.asList(
            new Incident("test", "CUSTOM", "Test Description", "2022-02-01T16:15Z", "2022-02-01T16:15Z"),
            new Incident("test", "CUSTOM", "Test Description", "2022-02-01T16:15Z", "2022-02-01T16:15Z")
    );

    public List<Incident> getIncidents() {
        return incidents;
    }

    public int getTotalIncidents() {
        return incidents.size();
    }
}
