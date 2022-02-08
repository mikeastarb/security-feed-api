package com.astarbia.securityapi.model;

import com.astarbia.securityapi.exception.RangeOutOfBoundsException;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Data
public class Incident {

    @NotEmpty
    private final String sourceID;

    @NotEmpty
    private final String sourceCode;

    @NotEmpty
    private final String description;

    @NotEmpty
    private final String publishedDate;

    @NotEmpty
    private final String lastModifiedDate;

    // TODO: Add tests and functionality around reference lists
    private final List<String> references;
    private Double latitude;
    private Double longitude;

    public Incident(String sourceID, String sourceCode, String description, String publishedDate, String lastModifiedDate) {
        this.sourceID = sourceID;
        this.sourceCode = sourceCode;
        this.description = description;
        this.publishedDate = publishedDate;
        this.lastModifiedDate = lastModifiedDate;
        this.references = new ArrayList<>();
        this.latitude = null;
        this.longitude = null;
    }

    public void setLatitude(double latitude) throws RangeOutOfBoundsException {
        if (latitude < -90.0 || 90.0 < latitude) {
            throw new RangeOutOfBoundsException();
        }
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) throws RangeOutOfBoundsException {
        if (longitude < -180.0 || 180.0 < longitude) {
            throw new RangeOutOfBoundsException();
        }
        this.longitude = longitude;
    }
}
