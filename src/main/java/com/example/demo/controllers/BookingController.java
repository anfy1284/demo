package com.example.demo.controllers;

import com.example.demo.classes.Room;
import com.example.demo.services.BookingService;
import com.example.demo.services.RoomService;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import com.example.demo.classes.Booking; // Ensure Booking is imported

@Controller
@DependsOn("dataInitializer") // Указываем зависимость от DataInitializer
public class BookingController {

    private final RoomService roomService;
    private final BookingService bookingService;

    public BookingController(RoomService roomService, BookingService bookingService) {
        this.roomService = roomService;
        this.bookingService = bookingService;
    }

    @GetMapping({"/bookings/{year}/{month}", "/bookings"})
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
}
