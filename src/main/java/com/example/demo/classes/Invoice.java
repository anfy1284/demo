package com.example.demo.classes;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Invoice implements Serializable {
    private String number;
    private String date;
    private List<String> bookingIds;
    private Map<String, Object> bill; // сохранённый bill (таблицы, суммы и т.д.)

    public Invoice() {}

    public Invoice(String number, String date, List<String> bookingIds, Map<String, Object> bill) {
        this.number = number;
        this.date = date;
        this.bookingIds = bookingIds;
        this.bill = bill;
    }

    public String getNumber() { return number; }

    public String getDate() { return date; }

    public List<String> getBookingIds() { return bookingIds; }
    public void setBookingIds(List<String> bookingIds) { this.bookingIds = bookingIds; }

    public Map<String, Object> getBill() { return bill; }
    public void setBill(Map<String, Object> bill) { this.bill = bill; }
}
