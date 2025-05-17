package com.example.demo.classes;

import java.io.Serializable;

public class InvoiceItem implements Serializable {
    private String description;
    private double amount;

    public InvoiceItem(String description, double amount) {
        this.description = description;
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
