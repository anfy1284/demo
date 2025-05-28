package com.example.demo.classes;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class RoomPricing {
    private String roomId; // ID комнаты
    private Map<Period, Map<Integer, Double>> pricing; // Цены в разрезе периода и количества гостей
    private double priceForChildrenUnder3 = 0.0; // Бесплатно для детей до 2 лет включительно
    private double priceForChildren3To5 = 10.0; // Фиксированная цена для детей от 3 до 5 лет
    private double finalCleaningFeeShortStay; // Стоимость уборки для короткого пребывания (до 3 ночей)
    private double kurbeitragUnder6 = 0.0; // Курортный сбор для детей до 5 лет включительно
    private double kurbeitrag6To15 = 1.0; // Курортный сбор для детей от 6 до 15 лет включительно
    private double kurbeitrag16AndOlder = 2.1; // Курортный сбор для лиц 16 лет и старше
    private double dogFeePerNight = 10.0; // Стоимость за собаку за ночь
    private double breakfastPriceUnder3 = 0.0; // Бесплатно для детей до 2 лет включительно
    private double breakfastPrice3To5 = 3.5; // Цена для детей от 3 до 5 лет
    private double breakfastPrice6To13 = 8.5; // Цена для детей от 6 до 13 лет
    private double breakfastPrice14AndOlder = 14.5; // Цена для лиц от 14 лет и старше

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

    public double getPriceForChildrenUnder3() {
        return priceForChildrenUnder3;
    }

    public void setPriceForChildrenUnder3(double priceForChildrenUnder3) {
        this.priceForChildrenUnder3 = priceForChildrenUnder3;
    }

    public double getPriceForChildren3To5() {
        return priceForChildren3To5;
    }

    public void setPriceForChildren3To5(double priceForChildren3To5) {
        this.priceForChildren3To5 = priceForChildren3To5;
    }

    public double getFinalCleaningFeeShortStay() {
        return finalCleaningFeeShortStay;
    }

    public void setFinalCleaningFeeShortStay(double finalCleaningFeeShortStay) {
        this.finalCleaningFeeShortStay = finalCleaningFeeShortStay;
    }

    public double getKurbeitragUnder6() {
        return kurbeitragUnder6;
    }

    public void setKurbeitragUnder6(double kurbeitragUnder6) {
        this.kurbeitragUnder6 = kurbeitragUnder6;
    }

    public double getKurbeitrag6To15() {
        return kurbeitrag6To15;
    }

    public void setKurbeitrag6To15(double kurbeitrag6To15) {
        this.kurbeitrag6To15 = kurbeitrag6To15;
    }

    public double getKurbeitrag16AndOlder() {
        return kurbeitrag16AndOlder;
    }

    public void setKurbeitrag16AndOlder(double kurbeitrag16AndOlder) {
        this.kurbeitrag16AndOlder = kurbeitrag16AndOlder;
    }

    public double getDogFeePerNight() {
        return dogFeePerNight;
    }

    public void setDogFeePerNight(double dogFeePerNight) {
        this.dogFeePerNight = dogFeePerNight;
    }

    public double getBreakfastPriceUnder3() {
        return breakfastPriceUnder3;
    }

    public void setBreakfastPriceUnder3(double breakfastPriceUnder3) {
        this.breakfastPriceUnder3 = breakfastPriceUnder3;
    }

    public double getBreakfastPrice3To5() {
        return breakfastPrice3To5;
    }

    public void setBreakfastPrice3To5(double breakfastPrice3To5) {
        this.breakfastPrice3To5 = breakfastPrice3To5;
    }

    public double getBreakfastPrice6To13() {
        return breakfastPrice6To13;
    }

    public void setBreakfastPrice6To13(double breakfastPrice6To13) {
        this.breakfastPrice6To13 = breakfastPrice6To13;
    }

    public double getBreakfastPrice14AndOlder() {
        return breakfastPrice14AndOlder;
    }

    public void setBreakfastPrice14AndOlder(double breakfastPrice14AndOlder) {
        this.breakfastPrice14AndOlder = breakfastPrice14AndOlder;
    }

    // Универсальный метод для получения стандартной цены завтрака (например, для взрослых)
    public double getBreakfastPrice() {
        return breakfastPrice14AndOlder;
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
