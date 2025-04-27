package com.example.demo;

import com.example.demo.services.RoomService;
import com.example.demo.services.BookingService;
import com.example.demo.services.GuestService;
import com.example.demo.services.RoomPricingService;
import com.example.demo.classes.Room;
import com.example.demo.classes.Booking;
import com.example.demo.classes.Guest;
import com.example.demo.classes.RoomPricing;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import java.time.LocalDate;
import java.util.List;

@Component
@Order(1) // Указываем, что этот компонент должен быть выполнен первым
public class DataInitializer {

    private final RoomService roomService;
    private final BookingService bookingService;
    private final GuestService guestService;
    private final RoomPricingService roomPricingService; // Добавляем сервис

    public DataInitializer(RoomService roomService, BookingService bookingService, GuestService guestService, RoomPricingService roomPricingService) {
        this.roomService = roomService;
        this.bookingService = bookingService;
        this.guestService = guestService;
        this.roomPricingService = roomPricingService; // Инициализируем сервис
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

        // Add test guests
        Guest guest1 = new Guest();
        guest1.setName("John Doe");
        guest1.setDateOfBirth("1985-05-15");
        guest1.setEmail("johndoe@example.com");
        guest1.setPhoneNumber("123-456-7890");
        guest1.setAddress("123 Main St, Springfield");
        guestService.add(guest1);

        Guest guest2 = new Guest();
        guest2.setName("Jane Smith");
        guest2.setDateOfBirth("1990-08-22");
        guest2.setEmail("janesmith@example.com");
        guest2.setPhoneNumber("987-654-3210");
        guest2.setAddress("456 Elm St, Springfield");
        guestService.add(guest2);

        Guest guest3 = new Guest();
        guest3.setName("Alice Johnson");
        guest3.setDateOfBirth("1975-12-05");
        guest3.setEmail("alicejohnson@example.com");
        guest3.setPhoneNumber("555-123-4567");
        guest3.setAddress("789 Oak St, Springfield");
        guestService.add(guest3);

        Guest guest4 = new Guest();
        guest4.setName("Bob Brown");
        guest4.setDateOfBirth("1980-03-10");
        guest4.setEmail("bobbrown@example.com");
        guest4.setPhoneNumber("444-987-6543");
        guest4.setAddress("321 Pine St, Springfield");
        guestService.add(guest4);

        Guest guest5 = new Guest();
        guest5.setName("Charlie Green");
        guest5.setDateOfBirth("2000-07-25");
        guest5.setEmail("charliegreen@example.com");
        guest5.setPhoneNumber("333-222-1111");
        guest5.setAddress("654 Maple St, Springfield");
        guestService.add(guest5);

        // Создание бронирований
        Booking booking1 = new Booking();
        booking1.setStartDate("2025-03-05");
        booking1.setEndDate("2025-03-06");
        booking1.setDescription("Booking for March 5-6, 2025");
        booking1.setCustomerName("John Doe");
        booking1.setCustomerAddress("Musterstraße 1, 10115 Berlin");
        booking1.setPrepayment(100.0); // Предоплата
        booking1.setStatus("booked");
        booking1.setRoom(roomService.findByName("Room 1"));
        booking1.setGuests(List.of(guest1, guest2));
        bookingService.add(booking1);

        Booking booking2 = new Booking();
        booking2.setStartDate("2025-03-15");
        booking2.setEndDate("2025-03-16");
        booking2.setDescription("Booking for March 15-16, 2025");
        booking2.setCustomerName("Jane Smith");
        booking2.setCustomerAddress("Hauptstraße 5, 80331 München");
        booking2.setPrepayment(150.0); // Предоплата
        booking2.setStatus("booked");
        booking2.setRoom(roomService.findByName("Room 2"));
        booking2.setGuests(List.of(guest3));
        bookingService.add(booking2);

        Booking booking3 = new Booking();
        booking3.setStartDate("2025-03-20");
        booking3.setEndDate("2025-03-21");
        booking3.setDescription("Booking for March 20-21, 2025");
        booking3.setCustomerName("Bob Brown");
        booking3.setCustomerAddress("Bahnhofstraße 10, 90402 Nürnberg");
        booking3.setPrepayment(200.0); // Предоплата
        booking3.setStatus("booked");
        booking3.setRoom(roomService.findByName("Room 1"));
        booking3.setGuests(List.of(guest4, guest5));
        bookingService.add(booking3);

        Booking booking4 = new Booking();
        booking4.setStartDate("2025-03-25");
        booking4.setEndDate("2025-03-26");
        booking4.setDescription("Booking for March 25-26, 2025");
        booking4.setCustomerName("Alice Johnson");
        booking4.setCustomerAddress("Schillerstraße 3, 70173 Stuttgart");
        booking4.setPrepayment(50.0); // Предоплата
        booking4.setStatus("booked");
        booking4.setRoom(roomService.findByName("Room 2"));
        booking4.setGuests(List.of(guest1));
        bookingService.add(booking4);

        Booking booking5 = new Booking();
        booking5.setStartDate("2025-03-30");
        booking5.setEndDate("2025-03-31");
        booking5.setDescription("Booking for March 30-31, 2025");
        booking5.setCustomerName("Charlie Green");
        booking5.setCustomerAddress("Goethestraße 7, 60313 Frankfurt am Main");
        booking5.setPrepayment(120.0); // Предоплата
        booking5.setStatus("booked");
        booking5.setRoom(roomService.findByName("Room 1"));
        booking5.setGuests(List.of(guest2, guest3));
        bookingService.add(booking5);

        // Additional test bookings for April
        Booking aprilBooking6 = new Booking();
        aprilBooking6.setStartDate("2025-04-03");
        aprilBooking6.setEndDate("2025-04-04");
        aprilBooking6.setDescription("Booking for April 3-4, 2025");
        aprilBooking6.setCustomerName("Eve Adams");
        aprilBooking6.setCustomerAddress("Friedrichstraße 12, 50667 Köln");
        aprilBooking6.setPrepayment(80.0); // Предоплата
        aprilBooking6.setStatus("booked");
        aprilBooking6.setRoom(roomService.findByName("Room 3"));
        aprilBooking6.setGuests(List.of(guest1, guest4));
        bookingService.add(aprilBooking6);

        Booking aprilBooking7 = new Booking();
        aprilBooking7.setStartDate("2025-04-11");
        aprilBooking7.setEndDate("2025-04-13");
        aprilBooking7.setDescription("Booking for April 11-13, 2025");
        aprilBooking7.setCustomerName("Frank Miller");
        aprilBooking7.setCustomerAddress("Kaiserstraße 8, 76133 Karlsruhe");
        aprilBooking7.setPrepayment(180.0); // Предоплата
        aprilBooking7.setStatus("booked");
        aprilBooking7.setRoom(roomService.findByName("Room 4"));
        aprilBooking7.setGuests(List.of(guest2, guest5));
        bookingService.add(aprilBooking7);

        Booking aprilBooking8 = new Booking();
        aprilBooking8.setStartDate("2025-04-17");
        aprilBooking8.setEndDate("2025-04-19");
        aprilBooking8.setDescription("Booking for April 17-19, 2025");
        aprilBooking8.setCustomerName("Grace Hopper");
        aprilBooking8.setCustomerAddress("Marktplatz 1, 28195 Bremen");
        aprilBooking8.setPrepayment(90.0); // Предоплата
        aprilBooking8.setStatus("booked");
        aprilBooking8.setRoom(roomService.findByName("Room 1"));
        aprilBooking8.setGuests(List.of(guest3));
        bookingService.add(aprilBooking8);

        Booking aprilBooking9 = new Booking();
        aprilBooking9.setStartDate("2025-04-22");
        aprilBooking9.setEndDate("2025-04-23");
        aprilBooking9.setDescription("Booking for April 22-23, 2025");
        aprilBooking9.setCustomerName("Henry Ford");
        aprilBooking9.setCustomerAddress("Rathausplatz 2, 86150 Augsburg");
        aprilBooking9.setPrepayment(70.0); // Предоплата
        aprilBooking9.setStatus("booked");
        aprilBooking9.setRoom(roomService.findByName("Room 2"));
        aprilBooking9.setGuests(List.of(guest4, guest5));
        bookingService.add(aprilBooking9);

        Booking aprilBooking10 = new Booking();
        aprilBooking10.setStartDate("2025-04-25");
        aprilBooking10.setEndDate("2025-04-27");
        aprilBooking10.setDescription("Booking for April 25-27, 2025");
        aprilBooking10.setCustomerName("Isabella Swan");
        aprilBooking10.setCustomerAddress("Altstadt 4, 90403 Nürnberg");
        aprilBooking10.setPrepayment(160.0); // Предоплата
        aprilBooking10.setStatus("booked");
        aprilBooking10.setRoom(roomService.findByName("Room 3"));
        aprilBooking10.setGuests(List.of(guest1, guest2, guest3));
        bookingService.add(aprilBooking10);

        // Создание объектов RoomPricing с использованием roomId
        RoomPricing room1Pricing = new RoomPricing(room1.getID());
        RoomPricing room2Pricing = new RoomPricing(room2.getID());
        RoomPricing room3Pricing = new RoomPricing(room3.getID());
        RoomPricing room4Pricing = new RoomPricing(room4.getID());

        // Периоды
        List<LocalDate[]> periods = List.of(
            new LocalDate[]{LocalDate.of(2024, 10, 6), LocalDate.of(2024, 12, 13)},
            new LocalDate[]{LocalDate.of(2024, 12, 13), LocalDate.of(2025, 3, 9)},
            new LocalDate[]{LocalDate.of(2025, 3, 9), LocalDate.of(2025, 5, 25)},
            new LocalDate[]{LocalDate.of(2025, 11, 9), LocalDate.of(2025, 12, 14)},
            new LocalDate[]{LocalDate.of(2026, 3, 8), LocalDate.of(2026, 5, 22)},
            new LocalDate[]{LocalDate.of(2026, 11, 8), LocalDate.of(2026, 12, 13)},
            new LocalDate[]{LocalDate.of(2027, 3, 7), LocalDate.of(2027, 5, 14)}
        );

        // Цены для комнаты 1
        for (LocalDate[] period : periods) {
            room1Pricing.addPrice(period[0], period[1], 1, 74.00);
            room1Pricing.addPrice(period[0], period[1], 2, 86.00);
            room1Pricing.addPrice(period[0], period[1], 3, 109.50);
            room1Pricing.addPrice(period[0], period[1], 4, 134.00);
            room1Pricing.addPrice(period[0], period[1], 5, 157.50);
        }

        // Цены для комнаты 2
        for (LocalDate[] period : periods) {
            room2Pricing.addPrice(period[0], period[1], 1, 84.00);
            room2Pricing.addPrice(period[0], period[1], 2, 97.00);
            room2Pricing.addPrice(period[0], period[1], 3, 114.00);
            room2Pricing.addPrice(period[0], period[1], 4, 139.00);
            room2Pricing.addPrice(period[0], period[1], 5, 162.50);
            room2Pricing.addPrice(period[0], period[1], 6, 186.00);
            room2Pricing.addPrice(period[0], period[1], 7, 210.00);
            room2Pricing.addPrice(period[0], period[1], 8, 234.00);
        }

        // Цены для комнаты 3
        for (LocalDate[] period : periods) {
            room3Pricing.addPrice(period[0], period[1], 1, 56.00);
            room3Pricing.addPrice(period[0], period[1], 2, 69.00);
            room3Pricing.addPrice(period[0], period[1], 3, 88.50);
            room3Pricing.addPrice(period[0], period[1], 4, 106.00);
        }

        // Цены для комнаты 4
        for (LocalDate[] period : periods) {
            room4Pricing.addPrice(period[0], period[1], 1, 56.00);
            room4Pricing.addPrice(period[0], period[1], 2, 69.00);
            room4Pricing.addPrice(period[0], period[1], 3, 88.50);
        }

        // Устанавливаем стоимость финальной уборки
        room1Pricing.setFinalCleaningFeeShortStay(40.00);
        room2Pricing.setFinalCleaningFeeShortStay(60.00);
        room3Pricing.setFinalCleaningFeeShortStay(30.00);
        room4Pricing.setFinalCleaningFeeShortStay(30.00);

        // Устанавливаем стоимость курортного сбора
        room1Pricing.setKurbeitragUnder6(0.0);
        room1Pricing.setKurbeitrag6To15(1.0);
        room1Pricing.setKurbeitrag16AndOlder(2.1);

        room2Pricing.setKurbeitragUnder6(0.0);
        room2Pricing.setKurbeitrag6To15(1.0);
        room2Pricing.setKurbeitrag16AndOlder(2.1);

        room3Pricing.setKurbeitragUnder6(0.0);
        room3Pricing.setKurbeitrag6To15(1.0);
        room3Pricing.setKurbeitrag16AndOlder(2.1);

        room4Pricing.setKurbeitragUnder6(0.0);
        room4Pricing.setKurbeitrag6To15(1.0);
        room4Pricing.setKurbeitrag16AndOlder(2.1);

        // Устанавливаем стоимость за собаку
        room1Pricing.setDogFeePerNight(10.0);
        room2Pricing.setDogFeePerNight(10.0);
        room3Pricing.setDogFeePerNight(10.0);
        room4Pricing.setDogFeePerNight(10.0);

        // Устанавливаем стоимость завтраков
        room1Pricing.setBreakfastPriceUnder3(0.0);
        room1Pricing.setBreakfastPrice3To5(3.5);
        room1Pricing.setBreakfastPrice6To13(8.5);
        room1Pricing.setBreakfastPrice14AndOlder(14.5);

        room2Pricing.setBreakfastPriceUnder3(0.0);
        room2Pricing.setBreakfastPrice3To5(3.5);
        room2Pricing.setBreakfastPrice6To13(8.5);
        room2Pricing.setBreakfastPrice14AndOlder(14.5);

        room3Pricing.setBreakfastPriceUnder3(0.0);
        room3Pricing.setBreakfastPrice3To5(3.5);
        room3Pricing.setBreakfastPrice6To13(8.5);
        room3Pricing.setBreakfastPrice14AndOlder(14.5);

        room4Pricing.setBreakfastPriceUnder3(0.0);
        room4Pricing.setBreakfastPrice3To5(3.5);
        room4Pricing.setBreakfastPrice6To13(8.5);
        room4Pricing.setBreakfastPrice14AndOlder(14.5);

        // Сохраняем объекты RoomPricing в RoomPricingService
        roomPricingService.addRoomPricing(room1Pricing);
        roomPricingService.addRoomPricing(room2Pricing);
        roomPricingService.addRoomPricing(room3Pricing);
        roomPricingService.addRoomPricing(room4Pricing);

        System.out.println("Data initialization completed.");
    }
}
