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
                        String formattedAddress = formatAddress(address);
                        suggestions.add(Map.of("formattedAddress", formattedAddress));
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

    private String formatAddress(Map<String, Object> address) {
        if (address == null) return "Unknown Address";

        String city = (String) address.getOrDefault("city", address.getOrDefault("town", address.getOrDefault("village", "")));
        String state = (String) address.getOrDefault("state", "");
        String country = (String) address.getOrDefault("country", "");
        String road = (String) address.getOrDefault("road", "");
        String houseNumber = (String) address.getOrDefault("house_number", "");

        // Формируем полный адрес
        return String.join(", ", List.of(houseNumber, road, city, state, country).stream()
                .filter(part -> part != null && !part.isEmpty())
                .toArray(String[]::new));
    }
}
