package com.astarbia.securityapi.service;

import com.astarbia.securityapi.exception.DuplicateValueException;
import com.astarbia.securityapi.model.Incident;
import com.astarbia.securityapi.repo.IncidentRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NvdService {
    private static final Logger logger = LoggerFactory.getLogger(NvdService.class);
    private final IncidentRepo incidentRepo;
    private final NvdHttpService nvdHttpService;
    private long lastRefreshedTime = 0;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final long TWO_HOUR_MS = 1000 * 60 * 2;

    public NvdService(IncidentRepo incidentRepo, NvdHttpService nvdHttpService) {
        this.incidentRepo = incidentRepo;
        this.nvdHttpService = nvdHttpService;
    }

    public void refreshNvds() {
        if(refreshNvds(System.currentTimeMillis() - lastRefreshedTime)) {
            lastRefreshedTime = System.currentTimeMillis();
        };
    }

    public boolean refreshNvds(long timeDelta) {
        if(timeDelta < TWO_HOUR_MS) {
            logger.info("Not refreshing NVDs yet, hasn't been two hours");
            return false;
        }

        JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readTree(nvdHttpService.getRecentCveDataString());
        } catch (JsonProcessingException e) {
            logger.error("Unable to parse the JSON String returned by the service. No processing will be done", e);
            return false;
        }

        int totalEntries = jsonNode.get("CVE_data_numberOfCVEs").asInt();
        for (int i = 0; i < totalEntries; i++) {
            JsonNode cveItem = jsonNode.get("CVE_Items").get(i);
            String cveID = cveItem.get("cve").get("CVE_data_meta").get("ID").asText();
            String description = "No English Description Found";

            int descriptionCount = cveItem.get("cve").get("description").get("description_data").size();
            for (int j = 0; j < descriptionCount; j++) {
                String lang = cveItem.get("cve").get("description").get("description_data").get(j).get("lang").asText();
                if (lang.equals("en")) {
                    description = cveItem.get("cve").get("description").get("description_data").get(j).get("value").asText();
                }
            }

            String publishedDate = cveItem.get("publishedDate").asText();
            String lastModifiedDate = cveItem.get("lastModifiedDate").asText();

            Incident newIncident = new Incident(cveID, "NVD", description, publishedDate, lastModifiedDate);
            try {
                incidentRepo.addIncident(newIncident);
            } catch (DuplicateValueException e) {
                logger.warn("Attempted to add a duplicate CVE on last refresh; ignoring", e);
            }
        }

        return true;
    }
}
