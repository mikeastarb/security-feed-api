package com.astarbia.securityapi;

import kong.unirest.json.JSONObject;

import java.io.IOException;
import java.util.zip.GZIPInputStream;

public class NvdSampleService {
    private JSONObject testCVEDataCache = null;
    private String testCVEJsonString = null;

    public JSONObject getTestCVEData() {
        if (testCVEDataCache == null) {
            testCVEDataCache = new JSONObject(getTestCVEJSONString());
        }

        return testCVEDataCache;
    }

    public String getTestCVEJSONString() {
        if (testCVEJsonString == null) {
            try {
                GZIPInputStream gzipInputStream = new GZIPInputStream(this.getClass().getClassLoader().getResourceAsStream("nvd-samples/nvdcve-1.1-recent.json.gz"));
                testCVEJsonString = new String(gzipInputStream.readAllBytes());
                gzipInputStream.close();
            } catch (IOException e) {
                throw new RuntimeException("Could not read test data file", e);
            }
        }

        return testCVEJsonString;
    }
}
