package com.example.demo.services;

import com.example.demo.classes.Invoice;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Service
public class InvoiceService {
    private static final String FILE = "invoices.dat";
    private List<Invoice> invoices = new ArrayList<>();

    public InvoiceService() {
        loadAll();
    }

    public synchronized void save(Invoice invoice) throws Exception {
        // Проверка уникальности номера
        for (Invoice inv : invoices) {
            if (inv.getNumber().equals(invoice.getNumber())) {
                throw new Exception("Счет с таким номером уже существует.");
            }
        }
        invoices.add(invoice);
        saveAll();
    }

    public synchronized void delete(String number) {
        invoices.removeIf(inv -> inv.getNumber().equals(number));
        saveAll();
    }

    public synchronized List<Invoice> getAll() {
        return new ArrayList<>(invoices);
    }

    public synchronized Invoice getByNumber(String number) {
        return invoices.stream().filter(i -> i.getNumber().equals(number)).findFirst().orElse(null);
    }

    private void saveAll() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE))) {
            out.writeObject(invoices);
        } catch (Exception ignored) {}
    }

    @SuppressWarnings("unchecked")
    private void loadAll() {
        File f = new File(FILE);
        if (!f.exists()) return;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE))) {
            invoices = (List<Invoice>) in.readObject();
        } catch (Exception ignored) {}
    }

    public synchronized String getNextInvoiceNumber() {
        int minNumber = 1216;
        int max = invoices.stream()
            .map(Invoice::getNumber)
            .filter(n -> n.matches("\\d+"))
            .mapToInt(Integer::parseInt)
            .max().orElse(minNumber - 1);
        return String.valueOf(max + 1);
    }
}
