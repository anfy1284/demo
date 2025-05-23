package com.example.demo.classes;

import lombok.Data;

@Data
public class Guest {
    private String ID;
    private String name;
    private String dateOfBirth;
    private String email;
    private String phoneNumber;
    private String address;

    public static boolean isValidAddress(String address) {
        return address != null && address.matches("^[a-zA-Z0-9\\s,.-]+$");
    }
}
