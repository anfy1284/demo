package com.example.demo.classes;

import java.io.Serializable;

public class RoomOrder implements Serializable {
    private static final long serialVersionUID = 1L;
    private String bezeichnung; // Наименование
    private double preis;       // Цена

    public RoomOrder() {}

    public RoomOrder(String bezeichnung, double preis) {
        this.bezeichnung = bezeichnung;
        this.preis = preis;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public double getPreis() {
        return preis;
    }

    public void setPreis(double preis) {
        this.preis = preis;
    }
}
