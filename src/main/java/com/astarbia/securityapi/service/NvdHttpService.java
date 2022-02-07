package com.astarbia.securityapi.service;

import org.springframework.stereotype.Service;

@Service
public class NvdHttpService {
    public String getRecentCveDataString() {
        return "{\"CVE_data_numberOfCVEs\":\"0\"}";
    }
}
