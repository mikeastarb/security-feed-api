package com.astarbia.securityapi.e2e;

import com.astarbia.securityapi.Application;
import kong.unirest.Unirest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class healthCheckIT {
    private static final Logger logger = LoggerFactory.getLogger(healthCheckIT.class);

    @LocalServerPort
    private int port;

    @Test
    public void healthCheckIsResponsive() {
        logger.info("Verifying that the health check page is returning 200");
        assertEquals(Unirest.get("http://localhost:" + port + "/actuator/health").asJson().getStatus(), 200);
    }
}
