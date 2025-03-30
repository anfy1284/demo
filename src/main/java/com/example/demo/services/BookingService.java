package com.example.demo.services;

import org.springframework.stereotype.Service;

import com.example.demo.classes.Booking;

@Service
public class BookingService extends BaseService<Booking> {

    @Override
    protected String getId(Booking booking) {
        return booking.getID();
    }

    @Override
    protected void setId(Booking booking, String id) {
        booking.setID(id);
    }
}
