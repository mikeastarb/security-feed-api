package com.astarbia.securityapi.unit.controller;

import com.astarbia.securityapi.controller.IncidentsController;
import com.astarbia.securityapi.repo.IncidentRepo;
import com.astarbia.securityapi.service.NvdPopulatorService;
import com.astarbia.securityapi.service.SourceDataServices;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class IncidentsControllerTest {

    @Test
    public void nvdsAreUpdatedWhenGettingIncidents() {
        IncidentRepo incidentRepo = new IncidentRepo();
        NvdPopulatorService nvdPopulatorService = Mockito.mock(NvdPopulatorService.class);
        SourceDataServices sourceDataServices = new SourceDataServices(nvdPopulatorService);

        IncidentsController controller = new IncidentsController(incidentRepo, sourceDataServices);
        controller.getAllIncidents(200, 0);

        Mockito.verify(nvdPopulatorService, Mockito.times(1)).refreshNvds();
    }
}
