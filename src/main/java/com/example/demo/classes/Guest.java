package com.example.demo.classes;

import java.io.Serializable;

public class Guest implements Serializable {
    private static final long serialVersionUID = 1L; // Add a unique serial version UID

    private String ID;
    private String name;
    private String dateOfBirth;
    private String email;
    private String phoneNumber;
    private String address;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public static boolean isValidAddress(String address) {
        return address != null && address.matches("^[a-zA-Z0-9\\s,.-]+$");
    }
}
