package com.example.demo.classes;

import lombok.Data;

@Data
public class Room {
    private String ID;
    private String name;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
