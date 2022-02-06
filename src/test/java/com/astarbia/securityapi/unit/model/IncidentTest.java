package com.astarbia.securityapi.unit.model;

import com.astarbia.securityapi.exception.RangeOutOfBoundsException;
import com.astarbia.securityapi.model.Incident;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Value;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class IncidentTest {

    @ParameterizedTest(name = "{index} => latitude={0}")
    @ValueSource(doubles = {-90.1, 90.1})
    public void latitudeCannotBeSetOutOfBounds(double latitude) {
        Incident incident = getTestIncident();
        assertThrows(RangeOutOfBoundsException.class, () -> {
            incident.setLatitude(latitude);
        });
    }

    @ParameterizedTest(name = "{index} => latitude={0}")
    @ValueSource(doubles = {-90, 0, 90})
    public void latitudeCanBeSetInBounds(double latitude) throws RangeOutOfBoundsException {
        Incident incident = getTestIncident();
        incident.setLatitude(latitude);
        assertThat(incident.getLatitude()).isEqualTo(latitude);
    }

    @ParameterizedTest(name = "{index} => longitude={0}")
    @ValueSource(doubles = {-180, 0, 180})
    public void longitudeCanBeSetInBounds(double longitude) throws RangeOutOfBoundsException {
        Incident incident = getTestIncident();
        incident.setLongitude(longitude);
        assertThat(incident.getLongitude()).isEqualTo(longitude);
    }

    @ParameterizedTest(name = "{index} => longitude={0}")
    @ValueSource(doubles = {-180.1, 180.1})
    public void longitudeCannotBeSetOutOfBounds(double longitude) {
        Incident incident = getTestIncident();
        assertThrows(RangeOutOfBoundsException.class, () -> {
            incident.setLongitude(longitude);
        });
    }

    private Incident getTestIncident() {
        return new Incident("test", "test", "test", "test", "test");
    }
}
