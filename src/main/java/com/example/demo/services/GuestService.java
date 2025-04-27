package com.example.demo.services;

import org.springframework.stereotype.Service;

import com.example.demo.classes.Guest;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GuestService extends BaseService<Guest> {

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
            .filter(guest -> guest.getName().equalsIgnoreCase(guestName) && guest.getDateOfBirth().equals(guestDob))
            .collect(Collectors.toList());
    }
}
