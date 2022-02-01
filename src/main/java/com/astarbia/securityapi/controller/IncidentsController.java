package com.astarbia.securityapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IncidentsController {

    @GetMapping(value = "/incidents", produces= { "application/json" })
    public String getAllIncidents() {
        return "true";
    }
}
