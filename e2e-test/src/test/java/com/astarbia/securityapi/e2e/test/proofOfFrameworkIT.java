package com.astarbia.securityapi.e2e.test;

import com.astarbia.securityapi.e2e.util.TestProperties;
import kong.unirest.Unirest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

public class proofOfFrameworkIT {
    private static final Logger logger = LoggerFactory.getLogger(proofOfFrameworkIT.class);
    private TestProperties testProps = new TestProperties();

    @Test
    public void simplePassingTest() {
        logger.info("Verifying a simple setup of the framework");
        Assert.assertTrue(true);
    }

    @Test
    public void healthCheckIsResponsive() {
        logger.info("Verifying that the health check page is returning 200");
        String baseUrl = testProps.getAppUrl();
        Assert.assertEquals(Unirest.get(baseUrl + "/actuator/health").asJson().getStatus(), 200);
    }
}
