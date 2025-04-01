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

        // Add new rooms
        Room room3 = new Room();
        room3.setName("Room 3");
        roomService.add(room3);

        Room room4 = new Room();
        room4.setName("Room 4");
        roomService.add(room4);

        // Создание бронирований
        Booking booking1 = new Booking();
        booking1.setStartDate("2025-03-05");
        booking1.setEndDate("2025-03-06");
        booking1.setDescription("Booking for March 5-6, 2025");
        booking1.setCustomerName("John Doe"); // Set customer name
        booking1.setStatus("booked"); // Set status
        booking1.setRoom(roomService.findByName("Room 1"));
        bookingService.add(booking1);

        Booking booking2 = new Booking();
        booking2.setStartDate("2025-03-15");
        booking2.setEndDate("2025-03-16");
        booking2.setDescription("Booking for March 15-16, 2025");
        booking2.setCustomerName("Jane Smith"); // Set customer name
        booking2.setStatus("booked"); // Set status
        booking2.setRoom(roomService.findByName("Room 2"));
        bookingService.add(booking2);

        Booking booking3 = new Booking();
        booking3.setStartDate("2025-03-20");
        booking3.setEndDate("2025-03-21");
        booking3.setDescription("Booking for March 20-21, 2025");
        booking3.setRoom(roomService.findByName("Room 1"));
        booking3.setStatus("booked"); // Set status
        bookingService.add(booking3);

        Booking booking4 = new Booking();
        booking4.setStartDate("2025-03-25");
        booking4.setEndDate("2025-03-26");
        booking4.setDescription("Booking for March 25-26, 2025");
        booking4.setRoom(roomService.findByName("Room 2"));
        booking4.setStatus("booked"); // Set status
        bookingService.add(booking4);

        Booking booking5 = new Booking();
        booking5.setStartDate("2025-03-30");
        booking5.setEndDate("2025-03-31");
        booking5.setDescription("Booking for March 30-31, 2025");
        booking5.setRoom(roomService.findByName("Room 1"));
        booking5.setStatus("booked"); // Set status
        bookingService.add(booking5);

        // Test bookings for Room 3 in March and April
        Booking room3MarchBooking = new Booking();
        room3MarchBooking.setStartDate("2025-03-10");
        room3MarchBooking.setEndDate("2025-03-12");
        room3MarchBooking.setDescription("Booking for Room 3, March 10-12, 2025");
        room3MarchBooking.setCustomerName("Frank Miller");
        room3MarchBooking.setStatus("booked");
        room3MarchBooking.setRoom(roomService.findByName("Room 3"));
        bookingService.add(room3MarchBooking);

        Booking room3AprilBooking = new Booking();
        room3AprilBooking.setStartDate("2025-04-07");
        room3AprilBooking.setEndDate("2025-04-09");
        room3AprilBooking.setDescription("Booking for Room 3, April 7-9, 2025");
        room3AprilBooking.setCustomerName("Grace Hopper");
        room3AprilBooking.setStatus("booked");
        room3AprilBooking.setRoom(roomService.findByName("Room 3"));
        bookingService.add(room3AprilBooking);

        // Test bookings for Room 4 in March and April
        Booking room4MarchBooking = new Booking();
        room4MarchBooking.setStartDate("2025-03-18");
        room4MarchBooking.setEndDate("2025-03-20");
        room4MarchBooking.setDescription("Booking for Room 4, March 18-20, 2025");
        room4MarchBooking.setCustomerName("Henry Ford");
        room4MarchBooking.setStatus("booked");
        room4MarchBooking.setRoom(roomService.findByName("Room 4"));
        bookingService.add(room4MarchBooking);

        Booking room4AprilBooking = new Booking();
        room4AprilBooking.setStartDate("2025-04-15");
        room4AprilBooking.setEndDate("2025-04-18");
        room4AprilBooking.setDescription("Booking for Room 4, April 15-18, 2025");
        room4AprilBooking.setCustomerName("Isabella Swan");
        room4AprilBooking.setStatus("booked");
        room4AprilBooking.setRoom(roomService.findByName("Room 4"));
        bookingService.add(room4AprilBooking);

        // Test bookings for April
        Booking aprilBooking1 = new Booking();
        aprilBooking1.setStartDate("2025-04-01");
        aprilBooking1.setEndDate("2025-04-02");
        aprilBooking1.setDescription("Booking for April 1-2, 2025");
        aprilBooking1.setCustomerName("Alice Johnson");
        aprilBooking1.setStatus("booked");
        aprilBooking1.setRoom(roomService.findByName("Room 1"));
        bookingService.add(aprilBooking1);

        Booking aprilBooking2 = new Booking();
        aprilBooking2.setStartDate("2025-04-05");
        aprilBooking2.setEndDate("2025-04-10"); // 6 days
        aprilBooking2.setDescription("Booking for April 5-10, 2025");
        aprilBooking2.setCustomerName("Bob Smith");
        aprilBooking2.setStatus("booked");
        aprilBooking2.setRoom(roomService.findByName("Room 2"));
        bookingService.add(aprilBooking2);

        Booking aprilBooking3 = new Booking();
        aprilBooking3.setStartDate("2025-04-15");
        aprilBooking3.setEndDate("2025-04-16");
        aprilBooking3.setDescription("Booking for April 15-16, 2025");
        aprilBooking3.setCustomerName("Charlie Brown");
        aprilBooking3.setStatus("booked");
        aprilBooking3.setRoom(roomService.findByName("Room 1"));
        bookingService.add(aprilBooking3);

        Booking aprilBooking4 = new Booking();
        aprilBooking4.setStartDate("2025-04-20");
        aprilBooking4.setEndDate("2025-04-24"); // 5 days
        aprilBooking4.setDescription("Booking for April 20-24, 2025");
        aprilBooking4.setCustomerName("Diana Prince");
        aprilBooking4.setStatus("booked");
        aprilBooking4.setRoom(roomService.findByName("Room 2"));
        bookingService.add(aprilBooking4);

        Booking aprilBooking5 = new Booking();
        aprilBooking5.setStartDate("2025-04-28");
        aprilBooking5.setEndDate("2025-04-30");
        aprilBooking5.setDescription("Booking for April 28-30, 2025");
        aprilBooking5.setCustomerName("Eve Adams");
        aprilBooking5.setStatus("booked");
        aprilBooking5.setRoom(roomService.findByName("Room 1"));
        bookingService.add(aprilBooking5);

        System.out.println("Data initialization completed.");
    }
}
