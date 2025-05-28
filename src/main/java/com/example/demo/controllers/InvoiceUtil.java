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
        // Группируем одинаковые позиции по label и taxRate
        Map<String, Map<String, Object>> merged = new LinkedHashMap<>();
        for (Map<String, Object> item : allItems) {
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
        // Можно добавить расчет итогов и налогов аналогично bill.ftlh
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
