package com.astarbia.securityapi.e2e;

import com.astarbia.securityapi.Application;
import com.astarbia.securityapi.e2e.util.IntTestBase;
import com.astarbia.securityapi.model.Incident;
import com.astarbia.securityapi.model.response.IncidentListResponse;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NvdIT extends IntTestBase {

    @Test
    void getIncidentsContainsRealRecentNVDData() throws IOException {
        byte[] responseBody = Unirest.get("https://nvd.nist.gov/feeds/json/cve/1.1/nvdcve-1.1-recent.json.gz")
                .asBytes()
                .getBody();

        GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(responseBody));
        JSONObject rootObject = new JSONObject(new String(gzipInputStream.readAllBytes()));

        int totalObjects = rootObject.getInt("CVE_data_numberOfCVEs");
        List<String> expectedIDs = new ArrayList<>(totalObjects);
        for (int i = 0; i < totalObjects; i++) {
            String cveID = rootObject.getJSONArray("CVE_Items").getJSONObject(i).getJSONObject("cve").getJSONObject("CVE_data_meta").getString("ID");
            expectedIDs.add(cveID);
        }

        IncidentListResponse actualIncidents = Unirest.get(buildUrl("/incidents"))
                .asObject(IncidentListResponse.class)
                .getBody();

        List<String> actualIDs = actualIncidents.getIncidents().stream().filter(incident -> incident.getSourceCode().equals("NVD"))
                .map(Incident::getSourceID).collect(Collectors.toList());

        assertThat(expectedIDs).containsAll(actualIDs);
    }
}
