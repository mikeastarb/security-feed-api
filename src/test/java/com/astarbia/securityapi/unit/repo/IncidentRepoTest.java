package com.astarbia.securityapi.unit.repo;

import com.astarbia.securityapi.exception.DuplicateValueException;
import com.astarbia.securityapi.model.Incident;
import com.astarbia.securityapi.repo.IncidentRepo;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class IncidentRepoTest {

    @Test
    public void newRepoStartsEmpty() {
        assertThat(new IncidentRepo().getIncidents().size()).isEqualTo(0);
    }

    @Test
    public void addIncidentToRepoPersists() throws DuplicateValueException {
        IncidentRepo repo = new IncidentRepo();
        Incident incident = new Incident("test", "test", "test", "test", "test");
        repo.addIncident(incident);
        assertThat(incident).isIn(repo.getIncidents());
    }

    @Test
    public void addIncidentReturnsIncidentAddedOnSuccess() throws DuplicateValueException {
        IncidentRepo repo = new IncidentRepo();
        Incident incident = new Incident("test", "test", "test", "test", "test");
        Incident addedIncident = repo.addIncident(incident);
        assertThat(incident).isEqualTo(addedIncident);
    }

    @Test
    public void cannotAddSameSourceIDCodePairTwice() throws DuplicateValueException {
        IncidentRepo repo = new IncidentRepo();
        Incident incident = new Incident("test", "test", "something", "different", "here");
        Incident secondIncident = new Incident("test", "test", "other", "data", "here");
        repo.addIncident(incident);
        assertThrows(DuplicateValueException.class, () -> {
            repo.addIncident(secondIncident);
        });
    }
}
