package com.example.demo.services;

import org.springframework.stereotype.Service;

import com.example.demo.classes.Guest;

import java.util.List;
import java.util.stream.Collectors;
import java.io.*;
import java.util.ArrayList;

@Service
public class GuestService extends BaseService<Guest> {
    private static final String GUESTS_FILE = "guests.dat";

    {
        Guest guest1 = new Guest();
        add(guest1);
        guest1.setName("Guest 1");

        Guest guest2 = new Guest();
        add(guest2);
        guest2.setName("Guest 2");
    }

    @Override
    protected String getId(Guest guest) {
        return guest.getID();
    }

    @Override
    protected void setId(Guest guest, String id) {
        guest.setID(id);
    }

    public List<Guest> findByName(String query) {
        return items.stream()
            .filter(guest -> guest.getName().toLowerCase().contains(query.toLowerCase()))
            .collect(Collectors.toList());
    }

    public List<Guest> findByNameAndDateOfBirth(String guestName, String guestDob) {
        return items.stream()
            .filter(guest -> guest.getName() != null && guest.getName().equalsIgnoreCase(guestName) &&
                             guest.getDateOfBirth() != null && guest.getDateOfBirth().equals(guestDob))
            .collect(Collectors.toList());
    }

    public void saveAllToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(GUESTS_FILE))) {
            oos.writeObject(new ArrayList<>(items));
        } catch (IOException e) {
            throw new RuntimeException("Failed to save guests to file: " + e.getMessage(), e);
        }
    }

    public void loadAllFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(GUESTS_FILE))) {
            List<Guest> loadedGuests = (List<Guest>) ois.readObject();
            for (Guest guest : loadedGuests) {
                add(guest, false); // Add to memory without triggering save
            }
        } catch (FileNotFoundException e) {
            System.out.println("Guests file not found. Starting with an empty list.");
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to load guests from file: " + e.getMessage(), e);
        }
    }

    @Override
    public void add(Guest guest) {
        add(guest, true); // Default behavior: save to file
    }

    public void add(Guest guest, boolean saveToFile) {
        super.add(guest);
        if (saveToFile) {
            saveAllToFile(); // Save to file only if explicitly requested
        }
    }
}
