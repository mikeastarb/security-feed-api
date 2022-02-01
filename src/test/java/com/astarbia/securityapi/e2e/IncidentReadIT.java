package com.astarbia.securityapi.e2e;

import com.astarbia.securityapi.Application;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IncidentReadIT {

    @LocalServerPort
    private int port;

    @Test
    public void incidentReadEndpointIsResponsive() {
        assertThat(Unirest.get("http://localhost:" + port + "/incidents").asJson().getStatus()).isEqualTo(200);
    }

    @Test
    public void incidentReadEndpointReturnsListOfIncidents() {
        // TODO: Refactor this to inject incident data via API call; move to an Insert incident test
        JSONObject responseJson = Unirest.get("http://localhost:" + port + "/incidents").asJson().getBody().getObject();

        int incidentResponseCount = responseJson.getInt("totalIncidents");
    }
}
