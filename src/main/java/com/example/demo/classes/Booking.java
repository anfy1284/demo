package com.example.demo.classes;

import java.util.List;
import java.time.LocalDate;

import lombok.Data;

@Data
public class Booking {
    private String ID;
    private Room room;
    private String customerName;
    private String startDate;
    private String endDate;
    private String status; // "booked", "checked-in", "checked-out", "cancelled"
    private String paymentStatus; // "paid", "pending", "refunded"
    private String paymentMethod; // "credit card", "cash", "bank transfer"
    private String specialRequests; // any special requests from the customer
    private List<Guest> guests; // list of guests associated with the booking
    private LocalDate date;
    private String description; // description of the booking
    private int duration; // Продолжительность бронирования в днях

    public String getRoomId() {
        return room != null ? room.getID() : null;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public static Booking createEmpty() {
        try {
            Booking booking = Booking.class.getDeclaredConstructor().newInstance();
            for (var field : Booking.class.getDeclaredFields()) {
                field.setAccessible(true);
                if (field.getType().equals(String.class)) {
                    field.set(booking, "");
                } else if (field.getType().equals(int.class)) {
                    field.set(booking, 0);
                } else if (field.getType().equals(LocalDate.class)) {
                    field.set(booking, LocalDate.MIN);
                } else if (field.getType().equals(List.class)) {
                    field.set(booking, List.of());
                } else {
                    field.set(booking, null);
                }
            }
            return booking;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create empty Booking instance", e);
        }
    }
}
