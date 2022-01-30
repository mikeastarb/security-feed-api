package com.astarbia.securityapi.e2e.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * The purpose of this tool is to try to find configuration from two sources in order. First it will try to find the
 * information from a local file called test.properties that can store non-sensitive configuration information on disk.
 * If it cannot be found in the local file, it then looks to see if the setting was put in the system properties
 *
 * Most often developing locally will use the test.properties file for ease of use, and then running from the command
 * line will be done entirely through system property definitions
 *
 * If a required property cannot be found, the system is designed to crash; there is no expectation that a user of this
 * will ever expect to gracefully recover from this situation
 */
public class TestProperties {
    private static final Logger logger = LoggerFactory.getLogger(TestProperties.class);
    private Properties testProperties = new Properties();
    private boolean testPropertiesLoadedOnce = false;

    public String getAppUrl() {
        return getRequiredProperty("app.url");
    }

    private String getRequiredProperty(String propertyName) {
        if (!testPropertiesLoadedOnce) {
            loadTestPropertiesFromFile();
        }

        if (!testProperties.containsKey(propertyName) && System.getProperties().containsKey(propertyName)) {
            testProperties.setProperty(propertyName, System.getProperties().getProperty(propertyName));
        }

        if (!testProperties.containsKey(propertyName)) {
            throw new RuntimeException("Property: " + propertyName + " could not be found. Please ensure it is set in the test.properties file or as a system property");
        }

        return testProperties.getProperty(propertyName);
    }

    private void loadTestPropertiesFromFile() {
        try {
            testProperties.load(this.getClass().getClassLoader().getResourceAsStream("test.properties"));
        } catch (IOException e) {
            logger.warn("Could not find a test.properties file in the classpath to read from, or reading the file had an error", e);
        }
    }
}
