package com.example.demo.services;

import com.example.demo.classes.RoomPricing;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class RoomPricingService {
    private final Map<String, RoomPricing> roomPricingMap = new HashMap<>();

    public void addRoomPricing(RoomPricing roomPricing) {
        roomPricingMap.put(roomPricing.getRoomId(), roomPricing);
    }

    public RoomPricing getRoomPricing(String roomId) {
        return roomPricingMap.get(roomId);
    }

    public boolean hasPricing(String roomId) {
        return roomPricingMap.containsKey(roomId);
    }

    // Универсальный расчет счета для одной брони (вынесено из контроллера/InvoiceUtil)
    public Map<String, Object> calculateBill(
            com.example.demo.classes.Room room,
            java.time.LocalDate startDate,
            java.time.LocalDate endDate,
            java.util.List<com.example.demo.classes.Guest> guests,
            int dogs,
            boolean includeBreakfast,
            double prepayment,
            java.util.List<com.example.demo.classes.RoomOrder> roomOrders
    ) {
        List<Map<String, Object>> billItems = new ArrayList<>();
        int adultGuestCount = 0, children3To5Count = 0, childrenUnder3Count = 0, children6To15Count = 0;
        java.time.LocalDate today = java.time.LocalDate.now();
        if (guests != null) {
            for (com.example.demo.classes.Guest g : guests) {
                if (g.getDateOfBirth() == null || g.getDateOfBirth().isEmpty()) continue;
                java.time.LocalDate birthDate = java.time.LocalDate.parse(g.getDateOfBirth());
                int age = today.getYear() - birthDate.getYear();
                if (today.isBefore(birthDate.plusYears(age))) age--;
                if (age <= 2) childrenUnder3Count++;
                else if (age >= 3 && age <= 5) children3To5Count++;
                else if (age >= 6 && age <= 15) children6To15Count++;
                else if (age >= 16) adultGuestCount++;
            }
        }
        long days = startDate.datesUntil(endDate.plusDays(1)).count() - 1;
        RoomPricing roomPricing = getRoomPricing(room.getID());
        if (roomPricing == null) return Map.of("main", List.of(Map.of("key", "error", "label", "Fehler", "value", "Kein Preis gefunden", "net", "0.00 €", "tax", "0.00 €", "taxRate", 19)));

        Double dailyPriceForAdults = roomPricing.getPrice(startDate, adultGuestCount + children6To15Count);
        if (dailyPriceForAdults == null) return Map.of("main", List.of(Map.of("key", "error", "label", "Fehler", "value", "Kein Preis für Gästezahl", "net", "0.00 €", "tax", "0.00 €", "taxRate", 19)));
        double accommodationPrice = dailyPriceForAdults * days;
        double priceForChildren3To5 = children3To5Count * roomPricing.getPriceForChildren3To5() * days;
        double dogFee = dogs * roomPricing.getDogFeePerNight() * days;

        double kurbeitragUnder6 = childrenUnder3Count * roomPricing.getKurbeitragUnder6() * days;
        double kurbeitrag6To15 = children6To15Count * roomPricing.getKurbeitrag6To15() * days;
        double kurbeitrag16AndOlder = adultGuestCount * roomPricing.getKurbeitrag16AndOlder() * days;

        // Основные услуги (ночевки)
        if (accommodationPrice > 0) {
            double gross = accommodationPrice;
            double net = gross / 1.07;
            double tax = gross - net;
            String label = days > 1
                ? days + " Übernachtungen vom " + startDate.format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yy")) +
                  " bis zum " + endDate.format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yy")) +
                  " " + (room != null ? room.getName() : "")
                : days + " Übernachtung аm " + startDate.format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yy")) +
                  " " + (room != null ? room.getName() : "");
            billItems.add(Map.of(
                "key", "accommodationPrice",
                "label", label,
                "value", String.format("%.2f €", gross),
                "net", String.format("%.2f €", net),
                "tax", String.format("%.2f €", tax),
                "taxRate", 7
            ));
        }
        if (priceForChildren3To5 > 0) {
            double gross = priceForChildren3To5;
            double net = gross / 1.07;
            double tax = gross - net;
            billItems.add(Map.of(
                "key", "children3To5Price",
                "label", "Kinderpreis (3-5 Jahre)",
                "value", String.format("%.2f €", gross),
                "net", String.format("%.2f €", net),
                "tax", String.format("%.2f €", tax),
                "taxRate", 7
            ));
        }
        if (includeBreakfast) {
            double breakfastPrice = roomPricing.getBreakfastPrice() * days;
            double gross = breakfastPrice;
            double net = gross / 1.19;
            double tax = gross - net;
            billItems.add(Map.of(
                "key", "breakfastPrice",
                "label", "Frühstück",
                "value", String.format("%.2f €", gross),
                "net", String.format("%.2f €", net),
                "tax", String.format("%.2f €", tax),
                "taxRate", 19
            ));
        }
        if (kurbeitragUnder6 > 0) {
            double gross = kurbeitragUnder6;
            double net = gross; // Нет налога
            double tax = 0.0;
            billItems.add(Map.of(
                "key", "kurbeitragUnder6",
                "label", "Kurbeitrag (unter 6 Jahre)",
                "value", String.format("%.2f €", gross),
                "net", String.format("%.2f €", net),
                "tax", String.format("%.2f €", tax),
                "taxRate", 0
            ));
        }
        if (kurbeitrag6To15 > 0) {
            double gross = kurbeitrag6To15;
            double net = gross; // Нет налога
            double tax = 0.0;
            billItems.add(Map.of(
                "key", "kurbeitrag6To15",
                "label", "Kurbeitrag (6-15 Jahre)",
                "value", String.format("%.2f €", gross),
                "net", String.format("%.2f €", net),
                "tax", String.format("%.2f €", tax),
                "taxRate", 0
            ));
        }
        if (kurbeitrag16AndOlder > 0) {
            double gross = kurbeitrag16AndOlder;
            double net = gross; // Нет налога
            double tax = 0.0;
            billItems.add(Map.of(
                "key", "kurbeitrag16AndOlder",
                "label", "Kurbeitrag (ab 16 Jahre)",
                "value", String.format("%.2f €", gross),
                "net", String.format("%.2f €", net),
                "tax", String.format("%.2f €", tax),
                "taxRate", 0
            ));
        }
        if (dogFee > 0) {
            double gross = dogFee;
            double net = gross / 1.19;
            double tax = gross - net;
            billItems.add(Map.of(
                "key", "dogFee",
                "label", "Hundepreis",
                "value", String.format("%.2f €", gross),
                "net", String.format("%.2f €", net),
                "tax", String.format("%.2f €", tax),
                "taxRate", 19
            ));
        }
        // ...roomOrders (если есть), финальные итоги...
        if (roomOrders != null && !roomOrders.isEmpty()) {
            for (com.example.demo.classes.RoomOrder order : roomOrders) {
                double gross = order.getPrice();
                double net = gross / 1.19;
                double tax = gross - net;
                billItems.add(Map.of(
                    "key", "roomOrder_" + order.getId(),
                    "label", order.getName(),
                    "value", String.format("%.2f €", gross),
                    "net", String.format("%.2f €", net),
                    "tax", String.format("%.2f €", tax),
                    "taxRate", 19
                ));
            }
        }

        // Итоговые строки
        double betrag = 0.0;
        for (Map<String, Object> item : billItems) betrag += parseEuro(item.get("value"));
        billItems.add(Map.of(
            "key", "totalPrice",
            "label", "Betrag",
            "value", String.format("%.2f €", betrag),
            "net", String.format("%.2f €", betrag),
            "tax", "0.00 €",
            "taxRate", 0
        ));
        billItems.add(Map.of(
            "key", "prepayment",
            "label", "Anzahlung",
            "value", String.format("%.2f €", prepayment),
            "net", String.format("%.2f €", prepayment),
            "tax", "0.00 €",
            "taxRate", 0
        ));
        billItems.add(Map.of(
            "key", "totalSum",
            "label", "Restbetrag",
            "value", String.format("%.2f €", betrag - prepayment),
            "net", String.format("%.2f €", betrag - prepayment),
            "tax", "0.00 €",
            "taxRate", 0
        ));

        // Суммирование одинаковых позиций
        Map<String, Map<String, Object>> merged = new LinkedHashMap<>();
        for (Map<String, Object> item : billItems) {
            String label = (String) item.get("label");
            Object taxRateObj = item.get("taxRate");
            String key = label + "|" + (taxRateObj != null ? taxRateObj.toString() : "null");
            double value = parseEuro(item.get("value"));
            double net = parseEuro(item.get("net"));
            double tax = parseEuro(item.get("tax"));
            if (merged.containsKey(key)) {
                Map<String, Object> prev = merged.get(key);
                prev.put("value", String.format("%.2f €", parseEuro(prev.get("value")) + value));
                prev.put("net", String.format("%.2f €", parseEuro(prev.get("net")) + net));
                prev.put("tax", String.format("%.2f €", parseEuro(prev.get("tax")) + tax));
            } else {
                merged.put(key, new LinkedHashMap<>(item));
            }
        }
        List<Map<String, Object>> main = new ArrayList<>(merged.values());
        Map<String, Object> result = new HashMap<>();
        result.put("main", main);
        return result;
    }

    private double parseEuro(Object euroString) {
        if (euroString == null) return 0.0;
        String str = euroString.toString().replace("€", "").replace(" ", "").replace(",", ".").trim();
        try {
            return Double.parseDouble(str);
        } catch (Exception e) {
            return 0.0;
        }
    }
}
