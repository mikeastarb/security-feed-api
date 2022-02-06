package com.astarbia.securityapi.unit.model.response;

import com.astarbia.securityapi.model.Incident;
import com.astarbia.securityapi.model.response.IncidentListResponse;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class IncidentListResponseTest {

    @Test
    public void settingResponseListUpdatesMetaInformation() {
        IncidentListResponse incidentListResponse = new IncidentListResponse();
        List<Incident> incidents = Arrays.asList(
                new Incident("test", "test", "test", "test", "test"),
                new Incident("test", "test", "test", "test", "test"),
                new Incident("test", "test", "test", "test", "test"),
                new Incident("test", "test", "test", "test", "test")
        );
        incidentListResponse.setIncidents(incidents);
        assertThat(incidentListResponse.getTotalIncidents()).isEqualTo(incidents.size());
    }

    @Test
    public void listResponseStartsEmpty() {
        IncidentListResponse incidentListResponse = new IncidentListResponse();
        assertThat(incidentListResponse.getTotalIncidents()).isEqualTo(0);
    }
}
