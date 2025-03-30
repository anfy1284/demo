package com.example.demo.services;

import org.springframework.stereotype.Service;

import com.example.demo.classes.Room;

@Service
public class RoomService extends BaseService<Room> {

    @Override
    protected String getId(Room room) {
        return room.getID();
    }

    @Override
    protected void setId(Room room, String id) {
        room.setID(id);
    }

    public Room findByName(String name) {
        return items.stream()
            .filter(room -> room.getName().equals(name))
            .findFirst()
            .orElse(null);
    }
}
