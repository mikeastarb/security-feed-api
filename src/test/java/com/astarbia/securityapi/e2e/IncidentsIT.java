package com.astarbia.securityapi.e2e;

import com.astarbia.securityapi.Application;
import com.astarbia.securityapi.e2e.util.IntTestBase;
import com.astarbia.securityapi.exception.RangeOutOfBoundsException;
import com.astarbia.securityapi.model.Incident;
import com.astarbia.securityapi.model.response.IncidentListResponse;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IncidentsIT extends IntTestBase {

    @Test
    public void incidentReadEndpointIsResponsive() {
        assertThat(Unirest.get(buildUrl("/incidents")).asJson().getStatus()).isEqualTo(200);
    }

    @Test
    public void postEndpointForIncidentsIsResponsive() {
        Incident incident = new Incident(UUID.randomUUID().toString(),
                "CUSTOM", "This is a Description", "2022-02-01T16:15Z", "2022-02-01T16:15Z");
        int responseCode = Unirest.post(buildUrl("/incidents"))
                .body(incident)
                .contentType("application/json")
                .asJson()
                .getStatus();
        assertThat(responseCode).isEqualTo(201);
    }

    @Test
    public void postIncidentReturnsIncidentCreated() {
        Incident incident = new Incident(UUID.randomUUID().toString(), "CUSTOM", "This is a Description", "Test", "Test");
        HttpResponse<Incident> incidentHttpResponse = Unirest.post(buildUrl("/incidents"))
                .body(incident)
                .contentType("application/json")
                .asObject(Incident.class);
        assertThat(incidentHttpResponse.getStatus()).isEqualTo(201);
        assertThat(incidentHttpResponse.getBody()).isEqualTo(incident);
    }

    @Test
    public void postIncidentWithMinimalDetails() {
        String randomIDString = UUID.randomUUID().toString();
        String incidentWithMinimalDetailJson = "{\"sourceID\":\"" + randomIDString + "\",\"sourceCode\":\"CUSTOM\",\"description\":\"This is a Description\",\"publishedDate\":\"Test\",\"lastModifiedDate\":\"Test\"}";
        Incident incident = new Incident(randomIDString, "CUSTOM", "This is a Description", "Test", "Test");
        HttpResponse<Incident> incidentHttpResponse = Unirest.post(buildUrl("/incidents"))
                .body(incidentWithMinimalDetailJson)
                .contentType("application/json")
                .asObject(Incident.class);
        assertThat(incidentHttpResponse.getStatus()).isEqualTo(201);
        assertThat(incidentHttpResponse.getBody()).isEqualTo(incident);
    }

    @Test
    public void postedIncidentIsAvailableViaRead() {
        Incident incident = new Incident(UUID.randomUUID().toString(), "CUSTOM", "This is a Description", "Test", "Test");
        Unirest.post(buildUrl("/incidents"))
                .body(incident)
                .contentType("application/json")
                .asJson();

        IncidentListResponse incidentList = Unirest.get(buildUrl("/incidents"))
                .asObject(IncidentListResponse.class)
                .getBody();

        assertThat(incident).isIn(incidentList.getIncidents());
    }

    @Test
    public void postIncidentWithTooFewDetailsFails() {
        String randomIDString = UUID.randomUUID().toString();
        String incidentWithTooFewDetails = "{\"sourceID\":\"" + randomIDString + "\",\"description\":\"This is a Description\",\"publishedDate\":\"Test\",\"lastModifiedDate\":\"Test\"}";
        HttpResponse<String> stringHttpResponse = Unirest.post(buildUrl("/incidents"))
                .body(incidentWithTooFewDetails)
                .contentType("application/json")
                .asString();

        assertThat(stringHttpResponse
                .getStatus()).isEqualTo(400);

        IncidentListResponse incidentListResponse = Unirest.get(buildUrl("/incidents"))
                .asObject(IncidentListResponse.class)
                .getBody();
        assertThat(randomIDString).isNotIn(incidentListResponse.getIncidents().stream().map(Incident::getSourceID));
    }

    @Test
    public void cannotPostTheSameIncidentTwice() {
        String randomIDString = UUID.randomUUID().toString();
        Incident first = new Incident(randomIDString, "CUSTOM", "Something", "Different", "Here");
        Incident second = new Incident(randomIDString, "CUSTOM", "Testing", "Other", "Things");

        Unirest.post(buildUrl("/incidents"))
                .body(first)
                .contentType("application/json")
                .asJson();

        HttpResponse<String> stringHttpResponse = Unirest.post(buildUrl("/incidents"))
                .body(second)
                .contentType("application/json")
                .asString();

        assertThat(stringHttpResponse
                .getStatus()).isEqualTo(208);

        IncidentListResponse getBody = Unirest.get(buildUrl("/incidents"))
                .asObject(IncidentListResponse.class)
                .getBody();

        assertThat(getBody.getIncidents().stream().filter(getIncident -> getIncident.getSourceID().equals(randomIDString)).count()).isEqualTo(1);
    }

    @Test
    public void addIncidentWithLocationData() throws RangeOutOfBoundsException {
        Incident incident = new Incident(UUID.randomUUID().toString(), "test", "test", "test", "test");
        incident.setLongitude(15);
        incident.setLatitude(-23.4);

        Incident response = Unirest.post(buildUrl("/incidents"))
                .body(incident)
                .contentType("application/json")
                .asObject(Incident.class)
                .getBody();

        assertThat(response).isEqualTo(incident);
    }

    @Test
    public void incidentsReturnLatestPublishedFirst() {
        IncidentListResponse response = Unirest.get(buildUrl("/incidents"))
                .asObject(IncidentListResponse.class)
                .getBody();

        List<Incident> expectedIncidents = new ArrayList<>(response.getIncidents());
        expectedIncidents.sort(Comparator.comparing(Incident::getPublishedDate).reversed());

        assertThat(response.getIncidents()).isEqualTo(expectedIncidents);
    }
}
