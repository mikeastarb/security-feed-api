package com.astarbia.securityapi.unit.service;

import com.astarbia.securityapi.service.NvdHttpService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

public class NvdHttpServiceTest {

    @Test
    public void httpServiceCallsOutToNVD() throws IOException {
        RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
        RestTemplateBuilder restTemplateBuilder = Mockito.mock(RestTemplateBuilder.class);
        Mockito.when(restTemplate.getForObject("https://nvd.nist.gov/feeds/json/cve/1.1/nvdcve-1.1-recent.json.gz", byte[].class))
                .thenReturn(this.getClass().getClassLoader().getResourceAsStream("nvd-samples/nvdcve-1.1-recent.json.gz").readAllBytes());
        Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);

        NvdHttpService nvdHttpService = new NvdHttpService(restTemplateBuilder);
        nvdHttpService.getRecentCveDataString();

        Mockito.verify(restTemplate, Mockito.times(1)).getForObject("https://nvd.nist.gov/feeds/json/cve/1.1/nvdcve-1.1-recent.json.gz", byte[].class);
    }
}
