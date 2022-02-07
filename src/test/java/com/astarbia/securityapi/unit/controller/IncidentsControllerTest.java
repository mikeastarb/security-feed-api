package com.astarbia.securityapi.unit.controller;

import com.astarbia.securityapi.controller.IncidentsController;
import com.astarbia.securityapi.repo.IncidentRepo;
import com.astarbia.securityapi.service.NvdService;
import com.astarbia.securityapi.service.SourceDataServices;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class IncidentsControllerTest {

    @Test
    public void nvdsAreUpdatedWhenGettingIncidents() {
        IncidentRepo incidentRepo = new IncidentRepo();
        NvdService nvdService = Mockito.mock(NvdService.class);
        SourceDataServices sourceDataServices = new SourceDataServices(nvdService);

        IncidentsController controller = new IncidentsController(incidentRepo, sourceDataServices);
        controller.getAllIncidents();

        Mockito.verify(nvdService, Mockito.times(1)).refreshNvds();
    }
}
