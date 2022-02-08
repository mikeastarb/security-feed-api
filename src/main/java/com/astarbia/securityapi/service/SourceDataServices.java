package com.astarbia.securityapi.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class SourceDataServices {

    private final NvdPopulatorService nvdPopulatorService;

    public SourceDataServices(NvdPopulatorService nvdPopulatorService) {
        this.nvdPopulatorService = nvdPopulatorService;
    }

    @PostConstruct
    public void refreshAllDataSources() {
        log.info("Refreshing all data sources");
        this.nvdPopulatorService.refreshNvds();
    }
}
