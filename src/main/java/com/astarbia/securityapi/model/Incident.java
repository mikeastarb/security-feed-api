package com.astarbia.securityapi.model;

import java.util.Arrays;
import java.util.List;

public class Incident {
    public String getSourceID() {
        return "test";
    }

    public String getSourceCode() {
        return "CUSTOM";
    }

    public List<String> getReferences() {
        return Arrays.asList("https://test.org/1");
    }

    public String getDescription() {
        return "Test Description";
    }

    public String getPublishedDate() {
        return "2022-02-01T16:15Z";
    }

    public String getLastModifiedDate() {
        return "2022-02-01T16:15Z";
    }

    public Long getLatitude() {
        return null;
    }

    public Long getLongitude() {
        return null;
    }
}
