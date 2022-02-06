package com.astarbia.securityapi.e2e;

import com.astarbia.securityapi.Application;
import com.astarbia.securityapi.model.Incident;
import com.astarbia.securityapi.model.response.IncidentListResponse;
import kong.unirest.Unirest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IncidentsIT {

    @LocalServerPort
    private int port;

    @Test
    public void incidentReadEndpointIsResponsive() {
        assertThat(Unirest.get("http://localhost:" + port + "/incidents").asJson().getStatus()).isEqualTo(200);
    }

    @Test
    public void incidentReadEndpointReturnsCountOfIncidentsInResponse() {
        // TODO: Refactor this to inject incident data via API call
        IncidentListResponse response = Unirest.get("http://localhost:" + port + "/incidents").asObject(IncidentListResponse.class).getBody();
        assertThat(response.getTotalIncidents()).isEqualTo(response.getIncidents().size());
    }

    @Test
    public void incidentReadEndpointReturnsIncidentData() {
        // TODO: Refactor this to inject incident data via API call
        IncidentListResponse response = Unirest.get("http://localhost:" + port + "/incidents").asObject(IncidentListResponse.class).getBody();
        Incident firstIncident = response.getIncidents().get(0);
        Incident secondIncident = response.getIncidents().get(1);

        assertThat(firstIncident.getSourceID()).isEqualTo("test");
        assertThat(firstIncident.getSourceCode()).isEqualTo("CUSTOM");
        assertThat(firstIncident.getReferences()).isEmpty();
        assertThat(firstIncident.getDescription()).isEqualTo("Test Description");
        assertThat(firstIncident.getPublishedDate()).isEqualTo("2022-02-01T16:15Z");
        assertThat(firstIncident.getLastModifiedDate()).isEqualTo("2022-02-01T16:15Z");
        assertThat(firstIncident.getLatitude()).isNull();
        assertThat(firstIncident.getLongitude()).isNull();

        assertThat(secondIncident.getSourceID()).isEqualTo("test");
    }

    @Test
    public void postEndpointForIncidentsIsResponsive() {
        Incident incident = new Incident("Test-123", "CUSTOM", "This is a Description", "2022-02-01T16:15Z", "2022-02-01T16:15Z");
        int responseCode = Unirest.post("http://localhost:" + port + "/incidents")
                .body(incident)
                .asJson()
                .getStatus();
        assertThat(responseCode).isEqualTo(200);
    }

    @Test
    public void postIncidentReturnsIncidentCreated() {
        Incident incident = new Incident("Test-123", "CUSTOM", "This is a Description", "Test", "Test");
        Incident responseBody = Unirest.post("http://localhost:" + port + "/incidents")
                .body(incident)
                .asObject(Incident.class)
                .getBody();
        assertThat(responseBody).isEqualTo(incident);
    }
}
