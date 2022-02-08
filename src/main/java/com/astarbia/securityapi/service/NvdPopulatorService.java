package com.astarbia.securityapi.service;

import com.astarbia.securityapi.exception.DuplicateValueException;
import com.astarbia.securityapi.model.Incident;
import com.astarbia.securityapi.repo.IncidentRepo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class NvdPopulatorService {
    private static final long TWO_HOUR_MS = 1000 * 60 * 2L;
    private final IncidentRepo incidentRepo;
    private final NvdHttpService nvdHttpService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private long lastRefreshedTime = 0;

    public NvdPopulatorService(IncidentRepo incidentRepo, NvdHttpService nvdHttpService) {
        this.incidentRepo = incidentRepo;
        this.nvdHttpService = nvdHttpService;
    }

    public void refreshNvds() {
        if (refreshNvds(System.currentTimeMillis() - lastRefreshedTime)) {
            lastRefreshedTime = System.currentTimeMillis();
        }
    }

    public boolean refreshNvds(long timeDelta) {
        if (timeDelta < TWO_HOUR_MS) {
            log.info("Not refreshing NVDs yet, hasn't been two hours");
            return false;
        }

        JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readTree(nvdHttpService.getRecentCveDataString());
        } catch (IOException e) {
            log.error("Unable to parse the JSON String returned by the service. No processing will be done", e);
            return false;
        }

        int totalEntries = jsonNode.get("CVE_data_numberOfCVEs").asInt();
        for (int i = 0; i < totalEntries; i++) {
            Incident newIncident = parseIncidentInformation(jsonNode.get("CVE_Items").get(i));
            try {
                incidentRepo.addIncident(newIncident);
            } catch (DuplicateValueException e) {
                log.warn("Attempted to add a duplicate CVE on last refresh; ignoring", e);
            }
        }

        return true;
    }

    private Incident parseIncidentInformation(JsonNode cveItem) {
        String cveID = cveItem.get("cve").get("CVE_data_meta").get("ID").asText();

        // TODO: Add test around this text being here
        String description = "No English Description Found";

        JsonNode descriptionDataList = cveItem.get("cve").get("description").get("description_data");
        int descriptionCount = descriptionDataList.size();
        for (int i = 0; i < descriptionCount; i++) {
            String lang = descriptionDataList.get(i).get("lang").asText();
            if (lang.equals("en")) {
                description = descriptionDataList.get(i).get("value").asText();
            }
        }

        String publishedDate = cveItem.get("publishedDate").asText();
        String lastModifiedDate = cveItem.get("lastModifiedDate").asText();

        return new Incident(cveID, "NVD", description, publishedDate, lastModifiedDate);
    }
}
