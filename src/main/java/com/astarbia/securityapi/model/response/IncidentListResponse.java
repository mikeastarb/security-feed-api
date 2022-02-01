package com.astarbia.securityapi.model.response;

import com.astarbia.securityapi.model.Incident;

import java.util.Collections;
import java.util.List;

public class IncidentListResponse {
    public List<Incident> getIncidents() {
        return Collections.emptyList();
    }

    public int getTotalIncidents() {
        return 0;
    }
}
