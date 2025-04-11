package com.example.demo.services;

import com.example.demo.classes.RoomPricing;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RoomPricingService {
    private final Map<String, RoomPricing> roomPricingMap = new HashMap<>();

    public void addRoomPricing(RoomPricing roomPricing) {
        roomPricingMap.put(roomPricing.getRoomId(), roomPricing);
    }

    public RoomPricing getRoomPricing(String roomId) {
        return roomPricingMap.get(roomId);
    }

    public boolean hasPricing(String roomId) {
        return roomPricingMap.containsKey(roomId);
    }
}
