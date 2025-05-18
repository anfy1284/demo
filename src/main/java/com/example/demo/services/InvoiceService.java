package com.example.demo.services;

import com.example.demo.classes.Invoice;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class InvoiceService extends BaseService<Invoice> {
    private static final String INVOICES_FILE = "invoices.dat";

    @Override
    protected String getId(Invoice invoice) {
        return invoice.getId();
    }

    @Override
    protected void setId(Invoice invoice, String id) {
        invoice.setId(id);
    }

    public void saveAllToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(INVOICES_FILE))) {
            oos.writeObject(new ArrayList<>(items));
        } catch (IOException e) {
            throw new RuntimeException("Failed to save invoices to file: " + e.getMessage(), e);
        }
    }

    public void loadAllFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(INVOICES_FILE))) {
            List<Invoice> loadedInvoices = (List<Invoice>) ois.readObject();
            for (Invoice invoice : loadedInvoices) {
                add(invoice, false); // Add to memory without triggering save
            }
        } catch (FileNotFoundException e) {
            System.out.println("Invoices file not found. Starting with an empty list.");
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to load invoices from file: " + e.getMessage(), e);
        }
    }

    @Override
    public void add(Invoice invoice) {
        add(invoice, true); // Default behavior: save to file
    }

    @Override
    protected void onItemAdded(Invoice invoice) {
        saveAllToFile();
    }

    public void delete(String invoiceId) {
        Invoice invoiceToDelete = findById(invoiceId);
        if (invoiceToDelete != null) {
            items.remove(invoiceToDelete);
            saveAllToFile();
        } else {
            throw new IllegalArgumentException("Invoice with ID " + invoiceId + " not found.");
        }
    }

    public Invoice findById(String id) {
        for (Invoice invoice : items) {
            if (invoice.getId().equals(id)) {
                return invoice;
            }
        }
        return null;
    }
}
