package com.astarbia.securityapi.e2e.util;

import org.springframework.boot.web.server.LocalServerPort;

public class IntTestBase {

    @LocalServerPort
    int localServerPort;

    protected String buildUrl(String path) {
        return "http://localhost:" + localServerPort + path;
    }
}
