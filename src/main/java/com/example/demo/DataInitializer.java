package com.example.demo;

import com.example.demo.services.RoomService;
import com.example.demo.services.BookingService;
import com.example.demo.classes.Room;
import com.example.demo.classes.Booking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

@Component
@Order(1) // Указываем, что этот компонент должен быть выполнен первым
public class DataInitializer {

    private final RoomService roomService;
    private final BookingService bookingService;

    @Autowired
    public DataInitializer(RoomService roomService, BookingService bookingService) {
        this.roomService = roomService;
        this.bookingService = bookingService;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationEvent() {
        System.out.println("Initializing data...");

        // Создание комнат
        Room room1 = new Room();
        room1.setName("Room 1");
        roomService.add(room1); // ID будет автоматически установлено в BaseService

        Room room2 = new Room();
        room2.setName("Room 2");
        roomService.add(room2); // ID будет автоматически установлено в BaseService

        // Создание бронирований
        Booking booking1 = new Booking();
        booking1.setStartDate("2025-03-05");
        booking1.setEndDate("2025-03-06");
        booking1.setDescription("Booking for March 5-6, 2025");
        booking1.setRoom(roomService.findByName("Room 1"));
        bookingService.add(booking1);

        Booking booking2 = new Booking();
        booking2.setStartDate("2025-03-15");
        booking2.setEndDate("2025-03-16");
        booking2.setDescription("Booking for March 15-16, 2025");
        booking2.setRoom(roomService.findByName("Room 2"));
        bookingService.add(booking2);

        Booking booking3 = new Booking();
        booking3.setStartDate("2025-03-20");
        booking3.setEndDate("2025-03-21");
        booking3.setDescription("Booking for March 20-21, 2025");
        booking3.setRoom(roomService.findByName("Room 1"));
        bookingService.add(booking3);

        Booking booking4 = new Booking();
        booking4.setStartDate("2025-03-25");
        booking4.setEndDate("2025-03-26");
        booking4.setDescription("Booking for March 25-26, 2025");
        booking4.setRoom(roomService.findByName("Room 2"));
        bookingService.add(booking4);

        Booking booking5 = new Booking();
        booking5.setStartDate("2025-03-30");
        booking5.setEndDate("2025-03-31");
        booking5.setDescription("Booking for March 30-31, 2025");
        booking5.setRoom(roomService.findByName("Room 1"));
        bookingService.add(booking5);

        System.out.println("Data initialization completed.");
    }
}
