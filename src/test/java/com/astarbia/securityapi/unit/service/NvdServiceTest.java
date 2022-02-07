package com.astarbia.securityapi.unit.service;

import com.astarbia.securityapi.NvdSampleService;
import com.astarbia.securityapi.model.Incident;
import com.astarbia.securityapi.repo.IncidentRepo;
import com.astarbia.securityapi.service.NvdHttpService;
import com.astarbia.securityapi.service.NvdService;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class NvdServiceTest {

    private NvdSampleService nvdSampleService = new NvdSampleService();

    @Test
    public void serviceAddsMissingIncidentsToRepo() {
        NvdHttpService nvdHttpService = Mockito.mock(NvdHttpService.class);
        Mockito.when(nvdHttpService.getRecentCveDataString()).thenReturn(nvdSampleService.getTestCVEJSONString());

        IncidentRepo incidentRepo = new IncidentRepo();
        NvdService nvdService = new NvdService(incidentRepo, nvdHttpService);
        nvdService.refreshNvds();

        JSONObject rootObject = nvdSampleService.getTestCVEData();
        int totalObjects = rootObject.getInt("CVE_data_numberOfCVEs");
        List<String> expectedIDs = new ArrayList<>(totalObjects);
        for (int i = 0; i < totalObjects; i++) {
            String cveID = rootObject.getJSONArray("CVE_Items").getJSONObject(i).getJSONObject("cve").getJSONObject("CVE_data_meta").getString("ID");
            expectedIDs.add(cveID);
        }

        List<String> actualIDs = incidentRepo.getIncidents().stream().map(Incident::getSourceID).collect(Collectors.toList());

        assertThat(actualIDs).containsAll(expectedIDs);
    }

    @Test
    public void serviceAddsAllNVDInformationForIncident() {
        NvdHttpService nvdHttpService = Mockito.mock(NvdHttpService.class);
        Mockito.when(nvdHttpService.getRecentCveDataString()).thenReturn(nvdSampleService.getTestCVEJSONString());

        IncidentRepo incidentRepo = new IncidentRepo();
        NvdService nvdService = new NvdService(incidentRepo, nvdHttpService);
        nvdService.refreshNvds();

        JSONObject rootObject = nvdSampleService.getTestCVEData();
        JSONObject firstCveObject = rootObject.getJSONArray("CVE_Items").getJSONObject(0);
        String cveId = firstCveObject.getJSONObject("cve").getJSONObject("CVE_data_meta").getString("ID");

        // TODO: Add Test Case For This
        String cveDescription = "No English Description Found";

        JSONArray descriptions = firstCveObject.getJSONObject("cve").getJSONObject("description").getJSONArray("description_data");
        for (int i = 0; i < descriptions.length(); i++) {
            String descriptionLanguage = descriptions.getJSONObject(i).getString("lang");
            if (descriptionLanguage.equals("en")) {
                cveDescription = descriptions.getJSONObject(i).getString("value");
                break;
            }
        }

        Incident firstIncident = new Incident(cveId, "NVD", cveDescription, firstCveObject.getString("publishedDate"), firstCveObject.getString("lastModifiedDate"));
        Incident expected = incidentRepo.getIncidents().stream().filter(incident -> incident.getSourceID().equals(firstIncident.getSourceID())).findAny().get();
        assertThat(firstIncident).isEqualTo(expected);
    }

    @Test
    public void nvdServiceDoesNotTryToReadDataTwiceBackToBack() {
        NvdHttpService nvdHttpService = Mockito.mock(NvdHttpService.class);
        Mockito.when(nvdHttpService.getRecentCveDataString()).thenReturn(nvdSampleService.getTestCVEJSONString());

        IncidentRepo incidentRepo = new IncidentRepo();
        NvdService nvdService = new NvdService(incidentRepo, nvdHttpService);
        nvdService.refreshNvds();
        nvdService.refreshNvds();

        Mockito.verify(nvdHttpService, Mockito.times(1)).getRecentCveDataString();
    }

    @Test
    public void nvdServiceDoesntConsiderFailuresForTimestampUpdates() {
        NvdHttpService nvdHttpService = Mockito.mock(NvdHttpService.class);
        Mockito.when(nvdHttpService.getRecentCveDataString()).thenReturn("broken json");

        IncidentRepo incidentRepo = new IncidentRepo();
        NvdService nvdService = new NvdService(incidentRepo, nvdHttpService);
        nvdService.refreshNvds();
        nvdService.refreshNvds();

        Mockito.verify(nvdHttpService, Mockito.times(2)).getRecentCveDataString();
    }

    private static long TWO_HOURS_MS = 1000 * 60 * 2;
    private static long TWO_HOURS_MS_MINUS_1 = TWO_HOURS_MS - 1;

    @Test
    public void nvdServiceRetriesDataLoadingAfterTwoHours() {
        NvdHttpService nvdHttpService = Mockito.mock(NvdHttpService.class);
        Mockito.when(nvdHttpService.getRecentCveDataString()).thenReturn(nvdSampleService.getTestCVEJSONString());

        IncidentRepo incidentRepo = new IncidentRepo();
        NvdService nvdService = new NvdService(incidentRepo, nvdHttpService);
        nvdService.refreshNvds(TWO_HOURS_MS_MINUS_1);
        nvdService.refreshNvds(TWO_HOURS_MS);

        Mockito.verify(nvdHttpService, Mockito.times(1)).getRecentCveDataString();
    }
}
