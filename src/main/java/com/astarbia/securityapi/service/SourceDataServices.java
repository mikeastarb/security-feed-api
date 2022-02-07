package com.astarbia.securityapi.service;

import org.springframework.stereotype.Component;

@Component
public class SourceDataServices {

    private NvdService nvdService;

    public SourceDataServices(NvdService nvdService) {
        this.nvdService = nvdService;
    }

    public void refreshAllDataSources() {
        this.nvdService.refreshNvds();
    }
}
