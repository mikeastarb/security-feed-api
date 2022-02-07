package com.astarbia.securityapi.model.response;

import com.astarbia.securityapi.model.Incident;

import java.util.Collections;
import java.util.List;

public class IncidentListResponse {
    private List<Incident> incidents = Collections.emptyList();
    private int page;
    private int size;
    private int totalIncidents;

    public List<Incident> getIncidents() {
        return incidents;
    }

    public void setIncidents(List<Incident> incidents) {
        this.incidents = incidents == null ? Collections.emptyList() : incidents;
    }

    public int getTotalIncidents() {
        return this.totalIncidents;
    }

    public void setTotalIncidents(int totalIncidents) {
        this.totalIncidents = totalIncidents;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getResponseCount() {
        return incidents.size();
    }
}
