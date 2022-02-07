package com.astarbia.securityapi.service;

import org.springframework.stereotype.Component;

@Component
public class SourceDataServices {

    private NvdPopulatorService nvdPopulatorService;

    public SourceDataServices(NvdPopulatorService nvdPopulatorService) {
        this.nvdPopulatorService = nvdPopulatorService;
    }

    public void refreshAllDataSources() {
        this.nvdPopulatorService.refreshNvds();
    }
}
