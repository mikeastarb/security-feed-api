package com.astarbia.securityapi.e2e;

import com.astarbia.securityapi.Application;
import com.astarbia.securityapi.e2e.util.IntTestBase;
import kong.unirest.Unirest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HealthCheckIT extends IntTestBase {

    @Test
    void healthCheckIsResponsive() {
        assertThat(Unirest.get(buildUrl("/actuator/health")).asJson().getStatus()).isEqualTo(200);
    }

    @Test
    void swaggerDocsAreAvailable() {
        assertThat(Unirest.get(buildUrl("/swagger-ui.html")).asString().getStatus()).isEqualTo(200);
    }
}
