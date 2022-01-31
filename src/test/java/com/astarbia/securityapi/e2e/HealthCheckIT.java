package com.astarbia.securityapi.e2e;

import com.astarbia.securityapi.Application;
import kong.unirest.Unirest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HealthCheckIT {
    private static final Logger logger = LoggerFactory.getLogger(HealthCheckIT.class);

    @LocalServerPort
    private int port;

    @Test
    public void healthCheckIsResponsive() {
        assertThat(Unirest.get("http://localhost:" + port + "/actuator/health").asJson().getStatus()).isEqualTo(200);
    }

    @Test
    public void swaggerDocsAreAvailable() {
        assertThat(Unirest.get("http://localhost:" + port + "/swagger-ui.html").asString().getStatus()).isEqualTo(200);
    }
}
