package com.example.demo.controllers;

import com.example.demo.classes.Room;
import com.example.demo.services.BookingService;
import com.example.demo.services.RoomService;
import com.example.demo.services.GuestService;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import com.example.demo.classes.Booking;
import com.example.demo.classes.Guest;
import java.util.ArrayList;

@Controller
@DependsOn("dataInitializer")
public class BookingController {

    private final RoomService roomService;
    private final BookingService bookingService;
    private final GuestService guestService;

    public BookingController(RoomService roomService, BookingService bookingService, GuestService guestService) {
        this.roomService = roomService;
        this.bookingService = bookingService;
        this.guestService = guestService;
    }

    @GetMapping({"/bookings/{year}/{month}", "/bookings", "/bookings/"})
    public String getBookings(
            @PathVariable(value = "year", required = false) String year,
            @PathVariable(value = "month", required = false) String month,
            Model model) {

        try {
            // Проверка на наличие комнат
            if (roomService.getAll().isEmpty()) {
                throw new IllegalStateException("Rooms are not initialized yet!");
            }

            // Проверка на наличие года и месяца
            if (month == null || year == null) {
                return "redirect:/bookings/" + LocalDate.now().getYear() + "/" + LocalDate.now().getMonthValue();
            }

            List<Room> rooms = roomService.getAll();
            LocalDate startDate = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), 1);
            LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

            // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM E", java.util.Locale.ENGLISH);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd", java.util.Locale.ENGLISH);
            List<String> formattedDatesOfMonth = startDate.datesUntil(endDate.plusDays(1))
                    .map(date -> date.format(formatter))
                    .collect(Collectors.toList());

            // Подготовка данных для отображения бронирований
            Map<String, Map<String, Booking>> bookingsMap = new HashMap<>();
            for (String date : formattedDatesOfMonth) {
                bookingsMap.put(date, new HashMap<>());
                for (Room room : rooms) {
                    bookingsMap.get(date).put(room.getID(), null);
                }
            }

            for (Booking booking : bookingService.getAll()) {
                LocalDate bookingStart = LocalDate.parse(booking.getStartDate());
                LocalDate bookingEnd = LocalDate.parse(booking.getEndDate());
                for (LocalDate date = bookingStart; !date.isAfter(bookingEnd); date = date.plusDays(1)) {
                    String dateKey = date.format(formatter);
                    if (bookingsMap.containsKey(dateKey)) {
                        bookingsMap.get(dateKey).put(booking.getRoomId(), booking);
                    }
                }
                // Добавляем информацию о продолжительности бронирования
                booking.setDuration((int) (bookingEnd.toEpochDay() - bookingStart.toEpochDay() + 1));
            }

            model.addAttribute("rooms", rooms);
            model.addAttribute("month", month);
            model.addAttribute("year", year);
            model.addAttribute("datesOfMonth", formattedDatesOfMonth);
            model.addAttribute("bookingsMap", bookingsMap);

            LocalDate previousMonth = startDate.minusMonths(1);
            LocalDate nextMonth = startDate.plusMonths(1);

            model.addAttribute("previousMonth", String.valueOf(previousMonth.getMonthValue()));
            model.addAttribute("previousYear", String.valueOf(previousMonth.getYear()));
            model.addAttribute("nextMonth", String.valueOf(nextMonth.getMonthValue()));
            model.addAttribute("nextYear", String.valueOf(nextMonth.getYear()));

            return "bookings";
        } catch (Exception e) {
            // Логирование ошибки
            System.err.println("Error in BookingController: " + e.getMessage());
            e.printStackTrace();

            // Возврат страницы ошибки
            model.addAttribute("errorMessage", e.getMessage() != null ? e.getMessage() : "An unexpected error occurred.");
            return "error";
        }
    }

    @GetMapping("/booking/{id}")
    public String editBookingForm(@PathVariable("id") String id, Model model) {
        try {
            Booking booking = bookingService.getById(id);
            if (booking == null) {
                throw new IllegalArgumentException("Booking not found for ID: " + id);
            }
            model.addAttribute("method", "edit");
            model.addAttribute("booking", booking);
            model.addAttribute("rooms", roomService.getAll());
            return "edit-booking";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage() != null ? e.getMessage() : "An unexpected error occurred.");
            return "error";
        }
    }

    @PostMapping("/booking/{id}")
    public String updateBooking(
            @PathVariable("id") String id,
            @RequestParam("roomId") String roomId,
            @RequestParam("customerName") String customerName,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam("description") String description,
            @RequestParam Map<String, String> allParams,
            Model model) {
        try {
            // Retrieve or create a new booking
            Booking booking = "new".equals(id) ? new Booking() : bookingService.getById(id);
            if (booking == null) {
                throw new IllegalArgumentException("Booking not found for ID: " + id);
            }

            // Retrieve the room
            Room room = roomService.getById(roomId);
            if (room == null) {
                throw new IllegalArgumentException("Room not found for ID: " + roomId);
            }

            // Update booking details
            booking.setRoom(room);
            booking.setCustomerName(customerName);
            booking.setStartDate(startDate);
            booking.setEndDate(endDate);
            booking.setDescription(description);

            // Process guests
            List<Guest> guests = new ArrayList<>();
            allParams.keySet().stream()
                .filter(key -> key.startsWith("guests[") && key.endsWith("].name"))
                .forEach(key -> {
                    String index = key.substring(7, key.indexOf("].name")); // Extract index from key
                    String guestName = allParams.get("guests[" + index + "].name");
                    String guestDob = allParams.get("guests[" + index + "].dateOfBirth");

                    if (guestName != null && guestDob != null) {
                        List<Guest> guestsFound = guestService.findByNameAndDateOfBirth(guestName, guestDob);
                        Guest guest = guestsFound.isEmpty() ? null : guestsFound.get(0);
                        if (guest == null) {
                            guest = new Guest();
                            guest.setName(guestName);
                            guest.setDateOfBirth(guestDob);
                            guestService.add(guest);
                        }
                        guests.add(guest);
                    }
                });
            booking.setGuests(guests);

            // Save the booking
            bookingService.add(booking);
            return "redirect:/bookings";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage() != null ? e.getMessage() : "An unexpected error occurred.");
            return "error";
        }
    }

    @GetMapping("/create-booking")
    public String createBookingForm(
            @RequestParam("date") String date,
            @RequestParam("roomId") String roomId,
            Model model) {
        try {
            Room room = roomService.getById(roomId);
            if (room == null) {
                throw new IllegalArgumentException("Room not found for ID: " + roomId);
            }

            Booking booking = Booking.createEmpty();

            booking.setRoom(room);
            booking.setStartDate(date);
            booking.setEndDate(date);

            model.addAttribute("method", "create");
            model.addAttribute("booking", booking);
            model.addAttribute("rooms", roomService.getAll());
            //model.addAttribute("query", "");
            return "edit-booking";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage() != null ? e.getMessage() : "An unexpected error occurred.");
            return "error";
        }
    }

    @PostMapping("/create-booking")
    public String createBooking(
            @RequestParam("date") String date,
            @RequestParam("roomId") String roomId,
            @RequestParam("customerName") String customerName,
            @RequestParam("description") String description,
            Model model) {
        try {
            Room room = roomService.getById(roomId);
            if (room == null) {
                throw new IllegalArgumentException("Room not found for ID: " + roomId);
            }

            Booking booking = new Booking();
            booking.setStartDate(date);
            booking.setEndDate(date); // Assuming single-day booking for simplicity
            booking.setCustomerName(customerName);
            booking.setDescription(description);
            booking.setRoom(room);
            booking.setStatus("booked");
            bookingService.add(booking);

            return "redirect:/booking/" + booking.getID();
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage() != null ? e.getMessage() : "An unexpected error occurred.");
            return "error";
        }
    }

    @GetMapping("/guests/search")
    @ResponseBody
    public List<Guest> searchGuests(@RequestParam("query") String query) {
        return guestService.findByName(query);
    }
}
