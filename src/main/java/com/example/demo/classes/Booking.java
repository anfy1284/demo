package com.example.demo.classes;

import java.util.List;
import java.time.LocalDate;
import java.util.ArrayList;

public class Booking {
    private String ID;
    private Room room;
    private String customerName;
    private String startDate;
    private String endDate;
    private String status;
    private String paymentStatus;
    private String paymentMethod;
    private String specialRequests;
    private List<Guest> guests;
    private LocalDate date;
    private String description;
    private int duration;
    private int dogs;
    private boolean includeBreakfast;
    // Новые поля для адреса клиента
    private String customerStreet;
    private String customerHouseNumber;
    private String customerPostalCode;
    private String customerCity;
    private String customerCountry;
    private double prepayment;
    private List<RoomOrder> roomOrders = new ArrayList<>(); // Neue Tabelle für Bestellungen im Zimmer

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getSpecialRequests() {
        return specialRequests;
    }

    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests;
    }

    public List<Guest> getGuests() {
        return guests;
    }

    public void setGuests(List<Guest> guests) {
        this.guests = guests;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getCustomerStreet() {
        return customerStreet;
    }

    public void setCustomerStreet(String customerStreet) {
        this.customerStreet = customerStreet;
    }

    public String getCustomerHouseNumber() {
        return customerHouseNumber;
    }

    public void setCustomerHouseNumber(String customerHouseNumber) {
        this.customerHouseNumber = customerHouseNumber;
    }

    public String getCustomerPostalCode() {
        return customerPostalCode;
    }

    public void setCustomerPostalCode(String customerPostalCode) {
        this.customerPostalCode = customerPostalCode;
    }

    public String getCustomerCity() {
        return customerCity;
    }

    public void setCustomerCity(String customerCity) {
        this.customerCity = customerCity;
    }

    public String getCustomerCountry() {
        return customerCountry;
    }

    public void setCustomerCountry(String customerCountry) {
        this.customerCountry = customerCountry;
    }

    public double getPrepayment() {
        return prepayment;
    }

    public void setPrepayment(double prepayment) {
        this.prepayment = prepayment;
    }

    public List<RoomOrder> getRoomOrders() {
        return roomOrders;
    }

    public void setRoomOrders(List<RoomOrder> roomOrders) {
        this.roomOrders = roomOrders;
    }

    public String getRoomId() {
        return room != null ? room.getID() : null;
    }

    public static Booking createEmpty() {
        try {
            Booking booking = new Booking();
            for (var field : Booking.class.getDeclaredFields()) {
                field.setAccessible(true);
                if (field.getType().equals(String.class)) {
                    field.set(booking, "");
                } else if (field.getType().equals(int.class)) {
                    field.set(booking, 0);
                } else if (field.getType().equals(boolean.class)) {
                    field.set(booking, false);
                } else if (field.getType().equals(LocalDate.class)) {
                    field.set(booking, null);
                } else if (field.getType().equals(List.class)) {
                    field.set(booking, new ArrayList<>());
                } else if (field.getType().equals(double.class)) {
                    field.set(booking, 0.0);
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
