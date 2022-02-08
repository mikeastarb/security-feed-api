package com.astarbia.securityapi.unit.repo;

import com.astarbia.securityapi.exception.DuplicateValueException;
import com.astarbia.securityapi.model.Incident;
import com.astarbia.securityapi.repo.IncidentRepo;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IncidentRepoTest {

    @Test
    void newRepoStartsEmpty() {
        assertThat(new IncidentRepo().getIncidents().size()).isZero();
    }

    @Test
    void addIncidentToRepoPersists() throws DuplicateValueException {
        IncidentRepo repo = new IncidentRepo();
        Incident incident = new Incident("test", "test", "test", "test", "test");
        repo.addIncident(incident);
        assertThat(incident).isIn(repo.getIncidents());
    }

    @Test
    void addIncidentReturnsIncidentAddedOnSuccess() throws DuplicateValueException {
        IncidentRepo repo = new IncidentRepo();
        Incident incident = new Incident("test", "test", "test", "test", "test");
        Incident addedIncident = repo.addIncident(incident);
        assertThat(incident).isEqualTo(addedIncident);
    }

    @Test
    void cannotAddSameSourceIDCodePairTwice() throws DuplicateValueException {
        IncidentRepo repo = new IncidentRepo();
        Incident incident = new Incident("test", "test", "something", "different", "here");
        Incident secondIncident = new Incident("test", "test", "other", "data", "here");
        repo.addIncident(incident);
        assertThrows(DuplicateValueException.class, () -> {
            repo.addIncident(secondIncident);
        });
    }

    @Test
    void defaultRepositorySortIsOnPublishedDate() throws DuplicateValueException {
        IncidentRepo repo = new IncidentRepo();
        Incident newerIncident = new Incident(UUID.randomUUID().toString(), "Test", "test", "2022-02-07T11:15Z", "2022-02-07T11:15Z");
        Incident olderIncident = new Incident(UUID.randomUUID().toString(), "Test", "test", "2022-02-06T11:15Z", "2022-02-07T11:15Z");

        repo.addIncident(olderIncident);
        repo.addIncident(newerIncident);

        assertThat(repo.getIncidents().get(0)).isEqualTo(newerIncident);
    }
}
