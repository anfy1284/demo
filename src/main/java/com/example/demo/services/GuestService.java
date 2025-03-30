package com.example.demo.services;

import org.springframework.stereotype.Service;

import com.example.demo.classes.Guest;

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
}
