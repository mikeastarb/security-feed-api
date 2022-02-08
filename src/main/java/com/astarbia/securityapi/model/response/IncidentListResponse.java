package com.astarbia.securityapi.model.response;

import com.astarbia.securityapi.model.Incident;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class IncidentListResponse {
    private List<Incident> incidents = Collections.emptyList();
    private int page;
    private int size;
    private int totalIncidents;

    public void setIncidents(List<Incident> incidents) {
        this.incidents = incidents == null ? Collections.emptyList() : incidents;
    }

    public int getResponseCount() {
        return this.incidents.size();
    }
}
