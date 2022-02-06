package com.astarbia.securityapi.model;

import com.astarbia.securityapi.exception.RangeOutOfBoundsException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Incident {

    private final String sourceID;
    private final String sourceCode;
    private String description;
    private String publishedDate;
    private String lastModifiedDate;
    private List<String> references;
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
        if(latitude < -90.0 || 90.0 < latitude) {
            throw new RangeOutOfBoundsException();
        }
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) throws RangeOutOfBoundsException {
        if(longitude < -180.0 || 180.0 < longitude) {
            throw new RangeOutOfBoundsException();
        }
        this.longitude = longitude;
    }

    public String getSourceID() {
        return sourceID;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public String getDescription() {
        return description;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public List<String> getReferences() {
        return references;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Incident incident = (Incident) o;
        return Objects.equals(sourceID, incident.sourceID) && Objects.equals(sourceCode, incident.sourceCode) && Objects.equals(description, incident.description) && Objects.equals(publishedDate, incident.publishedDate) && Objects.equals(lastModifiedDate, incident.lastModifiedDate) && Objects.equals(references, incident.references) && Objects.equals(latitude, incident.latitude) && Objects.equals(longitude, incident.longitude);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceID, sourceCode, description, publishedDate, lastModifiedDate, references, latitude, longitude);
    }

    @Override
    public String toString() {
        return "Incident{" +
                "sourceID='" + sourceID + '\'' +
                ", sourceCode='" + sourceCode + '\'' +
                ", description='" + description + '\'' +
                ", publishedDate='" + publishedDate + '\'' +
                ", lastModifiedDate='" + lastModifiedDate + '\'' +
                ", references=" + references +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
