package com.example.demo.classes;

import java.util.List;
import java.time.LocalDate;
import java.util.ArrayList;

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
    private int dogs; // Количество собак
    private boolean includeBreakfast; // Учитывать завтраки
    private String customerAddress; // Address of the customer
    private double prepayment; // Поле для хранения предоплаты

    public String getRoomId() {
        return room != null ? room.getID() : null;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDogs() {
        return dogs;
    }

    public void setDogs(int dogs) {
        this.dogs = dogs;
    }

    public boolean isIncludeBreakfast() {
        return includeBreakfast;
    }

    public void setIncludeBreakfast(boolean includeBreakfast) {
        this.includeBreakfast = includeBreakfast;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public double getPrepayment() {
        return prepayment;
    }

    public void setPrepayment(double prepayment) {
        this.prepayment = prepayment;
    }

    public static Booking createEmpty() {
        try {
            Booking booking = new Booking(); // Use the default constructor directly
            for (var field : Booking.class.getDeclaredFields()) {
                field.setAccessible(true);
                if (field.getType().equals(String.class)) {
                    field.set(booking, "");
                } else if (field.getType().equals(int.class)) {
                    field.set(booking, 0);
                } else if (field.getType().equals(boolean.class)) {
                    field.set(booking, false); // Initialize boolean fields to false
                } else if (field.getType().equals(LocalDate.class)) {
                    field.set(booking, null); // Set to null instead of LocalDate.MIN
                } else if (field.getType().equals(List.class)) {
                    field.set(booking, new ArrayList<>()); // Use a mutable list
                } else if (field.getType().equals(double.class)) {
                    field.set(booking, 0.0); // Initialize double fields to 0.0
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
