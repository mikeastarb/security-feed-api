package com.astarbia.securityapi.unit.controller;

import com.astarbia.securityapi.controller.IncidentsController;
import com.astarbia.securityapi.repo.IncidentRepo;
import com.astarbia.securityapi.service.NvdService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class IncidentsControllerTest {

    @Test
    public void nvdsAreUpdatedWhenGettingIncidents() {
        IncidentRepo incidentRepo = new IncidentRepo();
        NvdService nvdService = Mockito.mock(NvdService.class);

        IncidentsController controller = new IncidentsController(incidentRepo, nvdService);
        controller.getAllIncidents();

        Mockito.verify(nvdService, Mockito.times(1)).refreshNvds();
    }
}
