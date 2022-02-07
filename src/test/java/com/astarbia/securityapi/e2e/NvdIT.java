package com.astarbia.securityapi.e2e;

import com.astarbia.securityapi.Application;
import com.astarbia.securityapi.NvdSampleService;
import com.astarbia.securityapi.e2e.util.IntTestBase;
import com.astarbia.securityapi.model.Incident;
import com.astarbia.securityapi.model.response.IncidentListResponse;
import com.astarbia.securityapi.service.NvdHttpService;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class NvdIT extends IntTestBase {

    private NvdSampleService nvdSampleService = new NvdSampleService();

    @MockBean
    private NvdHttpService nvdHttpService;

    @Test
    public void getIncidentsContainsRecentNVDData() {
        Mockito.when(nvdHttpService.getRecentCveDataString()).thenReturn(nvdSampleService.getTestCVEJSONString());
        JSONObject rootObject = nvdSampleService.getTestCVEData();
        int totalObjects = rootObject.getInt("CVE_data_numberOfCVEs");
        List<String> expectedIDs = new ArrayList<>(totalObjects);
        for (int i = 0; i < totalObjects; i++) {
            String cveID = rootObject.getJSONArray("CVE_Items").getJSONObject(i).getJSONObject("cve").getJSONObject("CVE_data_meta").getString("ID");
            expectedIDs.add(cveID);
        }

        IncidentListResponse actualIncidents = Unirest.get(buildUrl("/incidents"))
                .asObject(IncidentListResponse.class)
                .getBody();

        List<String> actualIDs = actualIncidents.getIncidents().stream().map(Incident::getSourceID).collect(Collectors.toList());

        assertThat(actualIDs).containsAll(expectedIDs);
    }
}
