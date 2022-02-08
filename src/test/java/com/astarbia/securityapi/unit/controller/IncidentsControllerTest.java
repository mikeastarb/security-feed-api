package com.astarbia.securityapi.unit.controller;

import com.astarbia.securityapi.controller.IncidentsController;
import com.astarbia.securityapi.exception.AlreadyReportedException;
import com.astarbia.securityapi.exception.IncidentNotFoundException;
import com.astarbia.securityapi.model.Incident;
import com.astarbia.securityapi.model.response.IncidentListResponse;
import com.astarbia.securityapi.repo.IncidentRepo;
import com.astarbia.securityapi.service.NvdPopulatorService;
import com.astarbia.securityapi.service.SourceDataServices;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IncidentsControllerTest {

    @Test
    void nvdsAreUpdatedWhenGettingIncidents() {
        IncidentRepo incidentRepo = new IncidentRepo();
        NvdPopulatorService nvdPopulatorService = Mockito.mock(NvdPopulatorService.class);
        SourceDataServices sourceDataServices = new SourceDataServices(nvdPopulatorService);

        IncidentsController controller = new IncidentsController(incidentRepo, sourceDataServices);
        controller.getAllIncidents(Optional.empty(),200, 0);

        Mockito.verify(nvdPopulatorService, Mockito.times(1)).refreshNvds();
    }

    @Test
    void getAllIncidentsWithSizeAndPage() {
        IncidentRepo incidentRepo = new IncidentRepo();
        SourceDataServices sourceDataServices = Mockito.mock(SourceDataServices.class);

        IncidentsController controller = new IncidentsController(incidentRepo, sourceDataServices);
        generateIncidents(controller, "SAMPLE", 200);

        IncidentListResponse response = controller.getAllIncidents(Optional.empty(), 15, 3);

        assertThat(response.getResponseCount()).isEqualTo(15);
        assertThat(response.getPage()).isEqualTo(3);
    }

    @Test
    void getLastPartialPage() {
        IncidentRepo incidentRepo = new IncidentRepo();
        SourceDataServices sourceDataServices = Mockito.mock(SourceDataServices.class);

        IncidentsController controller = new IncidentsController(incidentRepo, sourceDataServices);
        generateIncidents(controller, "SAMPLE", 22);

        IncidentListResponse response = controller.getAllIncidents(Optional.empty(), 10, 2);

        assertThat(response.getResponseCount()).isEqualTo(2);
        assertThat(response.getPage()).isEqualTo(2);
        assertThat(response.getSize()).isEqualTo(10);
    }

    @Test
    void getIncidentByCodeAndId() {
        IncidentRepo incidentRepo = new IncidentRepo();
        SourceDataServices sourceDataServices = Mockito.mock(SourceDataServices.class);

        IncidentsController controller = new IncidentsController(incidentRepo, sourceDataServices);

        String testId = UUID.randomUUID().toString();
        Incident incident = new Incident(testId, "TEST", "test", "Test", "test");
        controller.addNewIncident(incident);

        Incident foundIncident = controller.getIncident("TEST", testId);

        assertThat(foundIncident).isEqualTo(incident);
    }

    @Test
    void errorThrownWhenIncidentNotFound() {
        IncidentRepo incidentRepo = new IncidentRepo();
        SourceDataServices sourceDataServices = Mockito.mock(SourceDataServices.class);

        IncidentsController controller = new IncidentsController(incidentRepo, sourceDataServices);

        String testId = UUID.randomUUID().toString();
        Incident incident = new Incident(testId, "TEST", "test", "Test", "test");
        controller.addNewIncident(incident);

        assertThrows(IncidentNotFoundException.class, () -> controller.getIncident("other", "check"));
    }

    @Test
    void errorThrownWhenAddingSameIncidentTwice() {
        IncidentRepo incidentRepo = new IncidentRepo();
        SourceDataServices sourceDataServices = Mockito.mock(SourceDataServices.class);

        IncidentsController controller = new IncidentsController(incidentRepo, sourceDataServices);

        String testId = UUID.randomUUID().toString();
        Incident incident = new Incident(testId, "TEST", "test", "Test", "test");
        controller.addNewIncident(incident);

        assertThrows(AlreadyReportedException.class, () -> controller.addNewIncident(incident));
    }

    private void generateIncidents(IncidentsController controller, String code, int count) {
        for(int i = 0; i < count; i++) {
            Incident incident = new Incident(UUID.randomUUID().toString(), code, "Test", "Test", "Test");
            controller.addNewIncident(incident);
        }

    }
}
