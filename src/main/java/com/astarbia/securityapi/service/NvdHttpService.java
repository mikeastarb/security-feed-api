package com.astarbia.securityapi.service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

@Service
@Slf4j
public class NvdHttpService {

    private RestTemplate restTemplate;

    public NvdHttpService(RestTemplateBuilder restTemplateBuilder) {
        restTemplate = restTemplateBuilder.build();
    }

    public String getRecentCveDataString() throws IOException {
        byte[] response = restTemplate.getForObject("https://nvd.nist.gov/feeds/json/cve/1.1/nvdcve-1.1-recent.json.gz", byte[].class);
        try {
            return new String(new GZIPInputStream(new ByteArrayInputStream(response)).readAllBytes());
        } catch (IOException e) {
            log.error("Could not read gzip file from NVD. Returning empty data set");
            throw e;
        }
    }
}
