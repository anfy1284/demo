package com.example.demo.controllers;

import com.example.demo.classes.Booking;
import com.example.demo.classes.Guest;
import com.example.demo.classes.RoomOrder;
import com.example.demo.classes.RoomPricing;
import com.example.demo.services.RoomPricingService;
import com.example.demo.services.RoomService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class InvoiceUtil {
    public static Map<String, Object> calculateInvoiceBill(List<Booking> bookings, RoomPricingService roomPricingService, RoomService roomService) {
        List<Map<String, Object>> allItems = new ArrayList<>();
        for (Booking booking : bookings) {
            com.example.demo.classes.Room room = booking.getRoom();
            java.time.LocalDate start = java.time.LocalDate.parse(booking.getStartDate());
            java.time.LocalDate end = java.time.LocalDate.parse(booking.getEndDate());
            List<com.example.demo.classes.Guest> guests = booking.getGuests();
            int dogs = booking.getDogs();
            boolean includeBreakfast = booking.isIncludeBreakfast();
            double prepayment = booking.getPrepayment();
            List<com.example.demo.classes.RoomOrder> roomOrders = booking.getRoomOrders();
            Map<String, Object> bill = roomPricingService.calculateBill(
                room, start, end, guests, dogs, includeBreakfast, prepayment, roomOrders
            );
            if (bill != null && bill.containsKey("main")) {
                List<Map<String, Object>> main = (List<Map<String, Object>>) bill.get("main");
                allItems.addAll(main);
            }
        }
        // Группируем одинаковые позиции по label и taxRate, но сохраняем порядок появления ключей
        // Определяем порядок ключей для группировки (например, Übernachtungen, Kinderpreis, Frühstück, Kurbeitrag, Hundепр., Endreinigung, Zusatzleistung, Betrag, Anzahlung, Restbetrag)
        List<String> keyOrder = List.of(
            "accommodationPrice",
            "children3To5Price",
            "breakfastPrice",
            "breakfastPriceUnder3",
            "breakfastPrice3To5",
            "breakfastPrice6To13",
            "breakfastPrice14AndOlder",
            "kurbeitragUnder6",
            "kurbeitrag6To15",
            "kurbeitrag16AndOlder",
            "dogFee",
            "finalCleaningFee",
            "roomOrder",
            "totalPrice",
            "prepayment",
            "totalSum"
        );
        Map<String, Map<String, Object>> merged = new LinkedHashMap<>();
        Map<String, String> keyToFirstKey = new HashMap<>(); // key -> first encountered key for ordering

        for (Map<String, Object> item : allItems) {
            String key = (String) item.get("key");
            String label = (String) item.get("label");
            Object taxRateObj = item.get("taxRate");
            String mergeKey = label + "|" + (taxRateObj != null ? taxRateObj.toString() : "null");
            double value = parseEuro(item.get("value"));
            double net = parseEuro(item.get("net"));
            double tax = parseEuro(item.get("tax"));
            if (merged.containsKey(mergeKey)) {
                Map<String, Object> prev = merged.get(mergeKey);
                prev.put("value", String.format("%.2f €", parseEuro(prev.get("value")) + value));
                prev.put("net", String.format("%.2f €", parseEuro(prev.get("net")) + net));
                prev.put("tax", String.format("%.2f €", parseEuro(prev.get("tax")) + tax));
            } else {
                merged.put(mergeKey, new LinkedHashMap<>(item));
                if (key != null && !keyToFirstKey.containsKey(key)) {
                    keyToFirstKey.put(key, mergeKey);
                }
            }
        }
        // Собираем итоговый список в правильном порядке
        List<Map<String, Object>> main = new ArrayList<>();
        for (String k : keyOrder) {
            for (String mergeKey : merged.keySet()) {
                Map<String, Object> item = merged.get(mergeKey);
                if (k.equals(item.get("key"))) {
                    main.add(item);
                }
            }
        }
        // Добавляем оставшиеся (если есть)
        for (String mergeKey : merged.keySet()) {
            Map<String, Object> item = merged.get(mergeKey);
            if (!main.contains(item)) {
                main.add(item);
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("main", main);

        // === Добавляем расчет итоговых сумм налогов по ставкам (mwst) ===
        Map<Integer, Double> mwstSums = new LinkedHashMap<>();
        Map<Integer, Double> mwstValues = new LinkedHashMap<>();
        for (Map<String, Object> item : main) {
            Object taxRateObj = item.get("taxRate");
            Object taxObj = item.get("tax");
            if (taxRateObj != null && taxObj != null) {
                int taxRate;
                try { taxRate = Integer.parseInt(taxRateObj.toString()); } catch (Exception e) { continue; }
                double tax = parseEuro(taxObj);
                if (taxRate > 0 && tax > 0.0) {
                    mwstSums.put(taxRate, mwstSums.getOrDefault(taxRate, 0.0) + tax);
                }
            }
        }
        List<Map<String, Object>> mwstList = new ArrayList<>();
        double mwstTotal = 0.0;
        for (Map.Entry<Integer, Double> entry : mwstSums.entrySet()) {
            // mwstList.add(Map.of(
            //     "label", "MwSt. " + entry.getKey() + "%",
            //     "value", String.format("%.2f €", entry.getValue())
            // ));
            mwstTotal += entry.getValue();
        }
        if (mwstTotal > 0.0) {
            mwstList.add(Map.of(
                "label", "MwSt. gesamt",
                "value", String.format("%.2f €", mwstTotal)
            ));
        }
        if (!mwstList.isEmpty()) {
            result.put("mwst", mwstList);
        }
        // === конец блока mwst ===

        return result;
    }

    private static double parseEuro(Object euroString) {
        if (euroString == null) return 0.0;
        String str = euroString.toString().replace("€", "").replace(" ", "").replace(",", ".").trim();
        try {
            return Double.parseDouble(str);
        } catch (Exception e) {
            return 0.0;
        }
    }
}

// ВАЖНО: Дублирование бизнес-логики расчета счета действительно плохо!
// Рекомендуется вынести расчет счета в отдельный сервис (например, BillService или RoomPricingService).
// Тогда и BookingController, и InvoiceUtil будут вызывать один и тот же метод, и не будет дублирования.
// Сейчас расчет дублируется в BookingController и здесь (InvoiceUtil).
