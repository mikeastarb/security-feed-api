package com.astarbia.securityapi.e2e;

import com.astarbia.securityapi.Application;
import com.astarbia.securityapi.model.Incident;
import com.astarbia.securityapi.model.response.IncidentListResponse;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(com.astarbia.securityapi.repo.IncidentRepo.class)
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
        Incident incident = new Incident(UUID.randomUUID().toString(),
                "CUSTOM", "This is a Description", "2022-02-01T16:15Z", "2022-02-01T16:15Z");
        int responseCode = Unirest.post("http://localhost:" + port + "/incidents")
                .body(incident)
                .contentType("application/json")
                .asJson()
                .getStatus();
        assertThat(responseCode).isEqualTo(200);
    }

    @Test
    public void postIncidentReturnsIncidentCreated() {
        Incident incident = new Incident(UUID.randomUUID().toString(),"CUSTOM", "This is a Description", "Test", "Test")
        ;
        HttpResponse<Incident> incidentHttpResponse = Unirest.post("http://localhost:" + port + "/incidents")
                .body(incident)
                .contentType("application/json")
                .asObject(Incident.class);
        assertThat(incidentHttpResponse.getStatus()).isEqualTo(200);
        assertThat(incidentHttpResponse.getBody()).isEqualTo(incident);
    }

    @Test
    public void postIncidentWithMinimalDetails() {
        String randomIDString = UUID.randomUUID().toString();
        String incidentWithMinimalDetailJson = "{\"sourceID\":\"" + randomIDString + "\",\"sourceCode\":\"CUSTOM\",\"description\":\"This is a Description\",\"publishedDate\":\"Test\",\"lastModifiedDate\":\"Test\"}";
        Incident incident = new Incident(randomIDString, "CUSTOM", "This is a Description", "Test", "Test");
        HttpResponse<Incident> incidentHttpResponse = Unirest.post("http://localhost:" + port + "/incidents")
                .body(incidentWithMinimalDetailJson)
                .contentType("application/json")
                .asObject(Incident.class);
        assertThat(incidentHttpResponse.getStatus()).isEqualTo(200);
        assertThat(incidentHttpResponse.getBody()).isEqualTo(incident);
    }

    @Test
    public void postedIncidentIsAvailableViaRead() {
        Incident incident = new Incident(UUID.randomUUID().toString(), "CUSTOM", "This is a Description", "Test", "Test");
        Unirest.post("http://localhost:" + port + "/incidents")
                .body(incident)
                .contentType("application/json")
                .asJson();

        IncidentListResponse incidentList = Unirest.get("http://localhost:" + port + "/incidents")
                .asObject(IncidentListResponse.class)
                .getBody();

        assertThat(incident).isIn(incidentList.getIncidents());
    }

    // postingNewIncidentAddsOneIncidentToRepo

    @Test
    public void postIncidentWithTooFewDetailsFails() {
        String randomIDString = UUID.randomUUID().toString();
        String incidentWithTooFewDetails = "{\"sourceID\":\"" + randomIDString + "\",\"description\":\"This is a Description\",\"publishedDate\":\"Test\",\"lastModifiedDate\":\"Test\"}";
        int status = Unirest.post("http://localhost:" + port + "/incidents")
                .body(incidentWithTooFewDetails)
                .contentType("application/json")
                .asJson()
                .getStatus();
        assertThat(status).isNotEqualTo(200);

        //TODO: Also check that the incident ID did not get added
    }

    // cannotPostSameIncidentIDSourceCombinationTwice
}
