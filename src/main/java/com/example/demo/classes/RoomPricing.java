package com.example.demo.classes;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class RoomPricing {
    private String roomId; // ID комнаты
    private Map<Period, Map<Integer, Double>> pricing; // Цены в разрезе периода и количества гостей

    public RoomPricing(String roomId) {
        this.roomId = roomId;
        this.pricing = new HashMap<>();
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public Map<Period, Map<Integer, Double>> getPricing() {
        return pricing;
    }

    public void setPricing(Map<Period, Map<Integer, Double>> pricing) {
        this.pricing = pricing;
    }

    // Добавить цену для определённого периода и количества гостей
    public void addPrice(LocalDate startDate, LocalDate endDate, int numberOfGuests, double price) {
        if (startDate == null || endDate == null || numberOfGuests <= 0 || price <= 0) {
            throw new IllegalArgumentException("Invalid input for adding price");
        }
        Period period = new Period(startDate, endDate);
        pricing.putIfAbsent(period, new HashMap<>());
        pricing.get(period).put(numberOfGuests, price);
        System.out.println("Added price: " + price + " for period: " + period + " and guests: " + numberOfGuests);
    }

    // Получить цену для определённого периода и количества гостей
    public Double getPrice(LocalDate date, int numberOfGuests) {
        if (pricing.isEmpty()) {
            System.out.println("Pricing is empty!");
        }
        for (Period period : pricing.keySet()) {
            if (period.includes(date)) {
                return pricing.get(period).getOrDefault(numberOfGuests, null);
            }
        }
        System.out.println("No price found for date: " + date + " and guests: " + numberOfGuests);
        return null; // Цена не найдена
    }

    // Вспомогательный класс для хранения периода
    public static class Period {
        private LocalDate startDate;
        private LocalDate endDate;

        public Period(LocalDate startDate, LocalDate endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public LocalDate getStartDate() {
            return startDate;
        }

        public void setStartDate(LocalDate startDate) {
            this.startDate = startDate;
        }

        public LocalDate getEndDate() {
            return endDate;
        }

        public void setEndDate(LocalDate endDate) {
            this.endDate = endDate;
        }

        // Проверить, входит ли дата в период
        public boolean includes(LocalDate date) {
            return (date.isEqual(startDate) || date.isAfter(startDate)) &&
                   (date.isEqual(endDate) || date.isBefore(endDate));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Period period = (Period) o;
            return startDate.equals(period.startDate) && endDate.equals(period.endDate);
        }

        @Override
        public int hashCode() {
            return startDate.hashCode() * 31 + endDate.hashCode();
        }
    }
}
