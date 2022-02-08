package com.astarbia.securityapi.e2e;

import com.astarbia.securityapi.Application;
import com.astarbia.securityapi.e2e.util.IntTestBase;
import com.astarbia.securityapi.model.Incident;
import com.astarbia.securityapi.model.response.IncidentListResponse;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IncidentsPagingIT extends IntTestBase {

    @Test
    public void userCanSetSizeOfReturnedResults() {
        IncidentListResponse response = Unirest.get(buildUrl("/incidents?size=10"))
                .asObject(IncidentListResponse.class)
                .getBody();

        assertThat(response.getIncidents().size()).isEqualTo(10);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    public void returnCountMustBePositive(int size) {
        HttpResponse<String> responseObject = Unirest.get(buildUrl("/incidents?size=" + size))
                .asString();

        assertThat(responseObject.getStatus()).isEqualTo(500);
    }

    @Test
    public void pageNumberAndCountOfResultsInPageInResponse() {
        IncidentListResponse response = Unirest.get(buildUrl("/incidents?size=10"))
                .asObject(IncidentListResponse.class)
                .getBody();

        assertThat(response.getPage()).isEqualTo(0);
        assertThat(response.getSize()).isEqualTo(10);
    }

    @Test
    public void userCanGoToAnyValidPage() {
        IncidentListResponse response = Unirest.get(buildUrl("/incidents?size=10&page=5"))
                .asObject(IncidentListResponse.class)
                .getBody();

        assertThat(response.getPage()).isEqualTo(5);
        assertThat(response.getSize()).isEqualTo(10);
    }

    @Test
    public void goingToNonExistentPageReturnsError() {
        HttpResponse<String> responseObject = Unirest.get(buildUrl("/incidents?size=10&page=-1"))
                .asString();

        assertThat(responseObject.getStatus()).isEqualTo(500);
    }

    @Test
    public void goingToTooHighAPageReturnsError() {
        HttpResponse<String> responseObject = Unirest.get(buildUrl("/incidents?size=10&page=5000"))
                .asString();

        assertThat(responseObject.getStatus()).isEqualTo(400);
    }

    @ParameterizedTest
    @ValueSource(ints = {200, 201})
    public void maximumOf200RecordsReturnedAtOnce(int size) {
        IncidentListResponse response = Unirest.get(buildUrl("/incidents?size=" + size))
                .asObject(IncidentListResponse.class)
                .getBody();

        assertThat(response.getSize()).isEqualTo(200);
    }

    @Test
    public void defaultSizeIs200() {
        IncidentListResponse response = Unirest.get(buildUrl("/incidents"))
                .asObject(IncidentListResponse.class)
                .getBody();

        assertThat(response.getSize()).isEqualTo(200);
    }

    @ParameterizedTest
    @ValueSource(strings = {"10.3", "test", "0.13"})
    public void badInputsReturnErrors(String sample) {
        HttpResponse<String> responseObject = Unirest.get(buildUrl("/incidents?size=" + sample + "10&page=" + sample))
                .asString();

        assertThat(responseObject.getStatus()).isEqualTo(400);
    }

    @Test
    public void itemsNotLostOnPageBoundaries() {
        List<Incident> pagedIncidents = new ArrayList<>();

        pagedIncidents.addAll(Unirest.get(buildUrl("/incidents?size=10&page=0"))
                .asObject(IncidentListResponse.class)
                .getBody().getIncidents());

        pagedIncidents.addAll(Unirest.get(buildUrl("/incidents?size=10&page=1"))
                .asObject(IncidentListResponse.class)
                .getBody().getIncidents());

        List<Incident> nonPagedIncidents = Unirest.get(buildUrl("/incidents?size=20&page=0"))
                .asObject(IncidentListResponse.class)
                .getBody().getIncidents();

        assertThat(pagedIncidents).isEqualTo(nonPagedIncidents);
    }

    @Test
    public void totalCountAccountsForAllItems() {
        List<Incident> pagedIncidents = new ArrayList<>();
        IncidentListResponse firstResponse = Unirest.get(buildUrl("/incidents?size=20&page=0"))
                .asObject(IncidentListResponse.class)
                .getBody();

        int responseCode = 200;
        int page = 0;
        while(responseCode == 200) {
            HttpResponse<IncidentListResponse> incidentListResponseHttpResponse = Unirest.get(buildUrl("/incidents?page=" + page++))
                    .asObject(IncidentListResponse.class);

            responseCode = incidentListResponseHttpResponse.getStatus();

            if(responseCode == 200) {
                pagedIncidents.addAll(incidentListResponseHttpResponse
                        .getBody().getIncidents());
            }
        }

        assertThat(firstResponse.getTotalIncidents()).isEqualTo(pagedIncidents.size());
    }

    @Test
    public void responseHasActualCount() {
        IncidentListResponse response = Unirest.get(buildUrl("/incidents?page=1"))
                .asObject(IncidentListResponse.class)
                .getBody();

        assertThat(response.getResponseCount()).isEqualTo(response.getIncidents().size());
    }
}
