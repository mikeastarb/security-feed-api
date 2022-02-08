package com.astarbia.securityapi.e2e;

import com.astarbia.securityapi.Application;
import com.astarbia.securityapi.e2e.util.IntTestBase;
import com.astarbia.securityapi.model.Incident;
import com.astarbia.securityapi.model.response.IncidentListResponse;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IncidentsFilterIT extends IntTestBase {

    @Test
    void getIncidentsOfTypeReturnsOnlyThatType() {
        Set<String> uniqueIncidentTypes = new HashSet<>();

        Incident testIncident = new Incident(UUID.randomUUID().toString(), "test", "test", "test", "test");
        Unirest.post(buildUrl("/incidents"))
                .body(testIncident)
                .contentType("application/json")
                .asString();

        int responseCode = 200;
        int page = 0;
        while (responseCode == 200) {
            HttpResponse<IncidentListResponse> incidentListResponseHttpResponse = Unirest.get(buildUrl("/incidents/test?page=" + page++))
                    .asObject(IncidentListResponse.class);
            responseCode = incidentListResponseHttpResponse.getStatus();
            if (responseCode == 200) {
                uniqueIncidentTypes.addAll(incidentListResponseHttpResponse.getBody().getIncidents().stream().map(Incident::getSourceCode).collect(Collectors.toList()));
            }
        }

        assertThat(uniqueIncidentTypes.size()).isEqualTo(1);
        assertThat(uniqueIncidentTypes.contains("NVD")).isFalse();
    }

    @Test
    void gettingTypeThatDoesntExistReturnsEmptyList() {
        IncidentListResponse responseBody = Unirest.get(buildUrl("/incidents/TESTSOMETHINGNOTFOUND"))
                .asObject(IncidentListResponse.class)
                .getBody();

        assertThat(responseBody.getIncidents()).isEmpty();
    }

    @Test
    void getIndividualIncidentByTypeAndID() {
        String testId = UUID.randomUUID().toString();
        Incident testIncident = new Incident(testId, "test", "test", "test", "test");
        Unirest.post(buildUrl("/incidents"))
                .body(testIncident)
                .contentType("application/json")
                .asString();

        Incident returnedIncident = Unirest.get(buildUrl("/incidents/test/" + testId))
                .asObject(Incident.class)
                .getBody();

        assertThat(returnedIncident).isEqualTo(testIncident);
    }

    @Test
    void notFoundIfSourceCodeAndIDDontMatch() {
        String testId = UUID.randomUUID().toString();
        Incident testIncident = new Incident(testId, "test", "test", "test", "test");
        Unirest.post(buildUrl("/incidents"))
                .body(testIncident)
                .contentType("application/json")
                .asString();

        int responseStatus = Unirest.get(buildUrl("/incidents/test/testId"))
                .asObject(Incident.class)
                .getStatus();

        assertThat(responseStatus).isEqualTo(404);
    }

    @Test
    void paginationWorksWithFilter() {
        IncidentListResponse responseBody = Unirest.get(buildUrl("/incidents/NVD?size=10&page=3"))
                .asObject(IncidentListResponse.class)
                .getBody();

        assertThat(responseBody.getResponseCount()).isEqualTo(10);
        assertThat(responseBody.getPage()).isEqualTo(3);
    }
}
