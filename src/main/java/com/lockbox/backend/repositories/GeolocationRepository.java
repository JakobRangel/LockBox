package com.lockbox.backend.repositories;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.client.RestTemplate;

public class GeolocationRepository {
    private static final RestTemplate restTemplate = new RestTemplate();

    public static String getClientIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getRemoteAddr();
        } else {
            ipAddress = ipAddress.split(",")[0];  // Consider the first IP in the list
        }
        return ipAddress;
    }

    public static String getCountryByIp(String ip) {
        String url = String.format("https://freeipapi.com/api/json/%s", ip);
        try {
            IpApiResponse response = restTemplate.getForObject(url, IpApiResponse.class);
            return response != null ? response.getCountryCode() : "Unknown";
        } catch (Exception e) {
            System.err.println("Failed to fetch country from IP: " + e.getMessage());
            return "Unknown";
        }
    }

    // Define a response class based on the API's JSON structure
    public static class IpApiResponse {
        @JsonProperty("countryCode")  // Correct mapping to JSON property
        private String countryCode;

        public String getCountryCode() {
            return countryCode;
        }

        public void setCountryName(String countryName) {
            this.countryCode = countryName;
        }
    }
}
