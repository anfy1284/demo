package com.example.demo.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/address")
public class AddressController {

    private static final String NOMINATIM_API_URL = "https://nominatim.openstreetmap.org/search";

    @PostMapping("/suggestions")
    public ResponseEntity<List<Map<String, String>>> getAddressSuggestions(@RequestBody Map<String, String> request) {
        String query = request.get("query");
        List<Map<String, String>> suggestions = new ArrayList<>();

        if (query != null && query.length() >= 2) {
            try {
                RestTemplate restTemplate = new RestTemplate();
                String url = NOMINATIM_API_URL + "?q=" + query + "&format=json&addressdetails=1&limit=5";
                List<Map<String, Object>> response = restTemplate.getForObject(url, List.class);

                if (response != null) {
                    for (Map<String, Object> result : response) {
                        String displayName = (String) result.get("display_name");
                        suggestions.add(Map.of("label", displayName, "value", displayName));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return error("Failed to fetch address suggestions", e);
            }
        }
        return ResponseEntity.ok(suggestions);
    }

    @PostMapping("/parse")
    public ResponseEntity<List<Map<String, String>>> parseAddress(@RequestBody Map<String, String> request) {
        String query = request.get("query");
        List<Map<String, String>> suggestions = new ArrayList<>();

        if (query != null && query.length() >= 2) {
            try {
                RestTemplate restTemplate = new RestTemplate();
                String url = NOMINATIM_API_URL + "?q=" + query + "&format=json&addressdetails=1&limit=5";
                List<Map<String, Object>> response = restTemplate.getForObject(url, List.class);

                if (response != null) {
                    for (Map<String, Object> result : response) {
                        Map<String, Object> address = (Map<String, Object>) result.get("address");
                        Map<String, String> formatted = formatAddressParts(address);
                        suggestions.add(formatted);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return error("Failed to parse address", e);
            }
        }
        return ResponseEntity.ok(suggestions);
    }

    private ResponseEntity<List<Map<String, String>>> error(String message, Exception e) {
        List<Map<String, String>> errorList = new ArrayList<>();
        errorList.add(Map.of("error", message, "details", e != null ? e.toString() : ""));
        return ResponseEntity.status(500).body(errorList);
    }

    private Map<String, String> formatAddressParts(Map<String, Object> address) {
        if (address == null) return Map.of();

        String street = (String) address.getOrDefault("road", "");
        String houseNumber = (String) address.getOrDefault("house_number", "");
        String postalCode = (String) address.getOrDefault("postcode", "");
        String city = (String) address.getOrDefault("city", address.getOrDefault("town", address.getOrDefault("village", "")));
        String country = (String) address.getOrDefault("country", "");

        return Map.of(
            "street", street,
            "houseNumber", houseNumber,
            "postalCode", postalCode,
            "city", city,
            "country", country
        );
    }
}
