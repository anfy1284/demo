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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import com.example.demo.classes.Booking;
import com.example.demo.classes.Guest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import com.example.demo.classes.RoomPricing; // Ensure this is the correct package for RoomPricing
import com.example.demo.services.RoomPricingService; // Ensure this is the correct package for RoomPricingService
import java.util.Arrays;
import com.example.demo.classes.RoomOrder;
import java.util.LinkedHashMap;

@Controller
@DependsOn("dataInitializer")
public class BookingController extends BaseErrorController {

    private final RoomService roomService;
    private final BookingService bookingService;
    private final GuestService guestService;
    private final RoomPricingService roomPricingService;

    // Сделать контроллер singleton и доступным через Spring
    @Autowired
    public BookingController(RoomService roomService, BookingService bookingService, GuestService guestService, RoomPricingService roomPricingService) {
        this.roomService = roomService;
        this.bookingService = bookingService;
        this.guestService = guestService;
        this.roomPricingService = roomPricingService;
        // Можно добавить инициализацию или логирование, если нужно
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

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd", java.util.Locale.ENGLISH);
            DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", java.util.Locale.ENGLISH);

            List<String> formattedDatesOfMonth = startDate.datesUntil(endDate.plusDays(1))
                    .map(date -> date.format(formatter))
                    .collect(Collectors.toList());

            List<String> displayDatesOfMonth = startDate.datesUntil(endDate.plusDays(1))
                    .map(date -> date.format(displayFormatter))
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
                    if (bookingsMap.containsKey(dateKey) && booking.getRoom() != null) {
                        bookingsMap.get(dateKey).put(booking.getRoom().getID(), booking);
                    }
                }
                // Добавляем информацию о продолжительности бронирования
                booking.setDuration((int) (bookingEnd.toEpochDay() - bookingStart.toEpochDay() + 1));
            }

            model.addAttribute("rooms", rooms);
            model.addAttribute("month", month);
            model.addAttribute("year", year);
            model.addAttribute("datesOfMonth", formattedDatesOfMonth);
            model.addAttribute("displayDatesOfMonth", displayDatesOfMonth);
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
            return error(model, e.getMessage() != null ? e.getMessage() : "An unexpected error occurred.", e.toString());
        }
    }

    @GetMapping("/booking/{id}")
    public String editBookingForm(@PathVariable("id") String id, Model model) {
        try {
            Booking booking = bookingService.getById(id);
            if (booking == null) {
                return error(model, "Booking not found for ID: " + id, null);
            }

            // // Рассчитываем счет для текущей брони
            // Map<String, Object> billSectionsRaw = calculateBill(
            //     booking.getRoom().getID(),
            //     booking.getStartDate(),
            //     booking.getEndDate(),
            //     (Integer) null,
            //     booking.getGuests().stream().map(Guest::getDateOfBirth).toList(),
            //     booking.getDogs(),
            //     id,
            //     booking.isIncludeBreakfast(),
            //     0.0 // Adding prepayment as the last argument
            // );
            // Map<String, List<Map<String, String>>> billSections = new HashMap<>();
            // billSectionsRaw.forEach((key, value) -> {
            //     if (value instanceof List) {
            //         billSections.put(key, (List<Map<String, String>>) value);
            //     }
            // });

            // // Отладочный вывод для проверки содержимого счета
            // System.out.println("Bill sections: " + billSections);

            model.addAttribute("method", "edit");
            model.addAttribute("booking", booking);
            model.addAttribute("rooms", roomService.getAll());
            // model.addAttribute("bill", billSections); // Передаем структуру счета в шаблон

            return "edit-booking";
        } catch (Exception e) {
            return error(model, e.getMessage() != null ? e.getMessage() : "An unexpected error occurred.", null);
        }
    }

    @PostMapping("/booking/{id}")
    public Object updateBooking(
            @PathVariable("id") String id,
            @RequestParam("roomId") String roomId,
            @RequestParam("customerName") String customerName,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "dogs", required = false, defaultValue = "0") int dogs,
            @RequestParam(value = "includeBreakfast", required = false, defaultValue = "false") boolean includeBreakfast,
            @RequestParam(value = "customerStreet", required = false) String customerStreet,
            @RequestParam(value = "customerHouseNumber", required = false) String customerHouseNumber,
            @RequestParam(value = "customerPostalCode", required = false) String customerPostalCode,
            @RequestParam(value = "customerCity", required = false) String customerCity,
            @RequestParam(value = "customerCountry", required = false) String customerCountry,
            @RequestParam(value = "prepayment", required = false, defaultValue = "0") double prepayment,
            @RequestParam Map<String, String> allParams,
            jakarta.servlet.http.HttpServletRequest request,
            Model model) {
        try {
            // Проверка дат
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            if (start.isAfter(end)) {
                throw new IllegalArgumentException("Start date cannot be later than end date.");
            }

            // Проверка на дублирующихся гостей
            List<String> guestNames = allParams.keySet().stream()
                .filter(key -> key.startsWith("guests[") && key.endsWith("].name"))
                .map(allParams::get)
                .collect(Collectors.toList());
            Set<String> duplicateGuests = guestNames.stream()
                .filter(name -> Collections.frequency(guestNames, name) > 1)
                .collect(Collectors.toSet());
            if (!duplicateGuests.isEmpty()) {
                throw new IllegalArgumentException("Duplicate guests are not allowed: " + String.join(", ", duplicateGuests));
            }

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
            booking.setCustomerName(customerName != null ? customerName : "");
            booking.setStartDate(startDate);
            booking.setEndDate(endDate);
            booking.setDescription(description);
            booking.setDogs(dogs);
            booking.setIncludeBreakfast(includeBreakfast);
            booking.setCustomerStreet(customerStreet != null ? customerStreet : "");
            booking.setCustomerHouseNumber(customerHouseNumber != null ? customerHouseNumber : "");
            booking.setCustomerPostalCode(customerPostalCode != null ? customerPostalCode : "");
            booking.setCustomerCity(customerCity != null ? customerCity : "");
            booking.setCustomerCountry(customerCountry != null ? customerCountry : "");
            booking.setPrepayment(prepayment);

            // Debug: Log all keys in allParams
            System.out.println("All parameters: " + allParams.keySet());

            // Process guests
            List<Guest> guests = new ArrayList<>();
            allParams.keySet().stream()
                .filter(key -> key.startsWith("guests[") && key.endsWith("].name"))
                .forEach(key -> {
                    System.out.println("Processing key: " + key); // Debug: Log the key being processed
                    String index = key.substring(7, key.indexOf("].name")); // Extract index from key
                    String guestName = allParams.get("guests[" + index + "].name");
                    String guestDob = allParams.get("guests[" + index + "].dateOfBirth");

                    if (guestName != null && guestDob != null) {
                        System.out.println("Found guest: " + guestName + ", DOB: " + guestDob); // Debug: Log guest details
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

            // Process room orders
            List<RoomOrder> roomOrders = new ArrayList<>();
            allParams.keySet().stream()
                .filter(key -> key.startsWith("roomOrders[") && key.endsWith("].bezeichnung"))
                .forEach(key -> {
                    String index = key.substring(11, key.indexOf("].bezeichnung"));
                    String bezeichnung = allParams.get("roomOrders[" + index + "].bezeichnung");
                    String preisStr = allParams.get("roomOrders[" + index + "].preis");
                    double preis = 0.0;
                    try {
                        preis = preisStr != null && !preisStr.isEmpty() ? Double.parseDouble(preisStr) : 0.0;
                    } catch (NumberFormatException ignored) {}
                    if (bezeichnung != null && !bezeichnung.isEmpty()) {
                        roomOrders.add(new RoomOrder(bezeichnung, preis));
                    }
                });
            booking.setRoomOrders(roomOrders);

            // Save the booking
            bookingService.add(booking);

            // Если AJAX-запрос, возвращаем JSON с bookingId
            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                Map<String, String> result = new HashMap<>();
                result.put("bookingId", booking.getID());
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(result);
            }

            return "redirect:/bookings";
        } catch (Exception e) {
            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("error", e.getMessage() != null ? e.getMessage() : "Fehler beim Speichern der Buchung"));
            }
            return error(model, e.getMessage() != null ? e.getMessage() : "An unexpected error occurred.", null);
        }
    }

    @GetMapping("/create-booking")
    public String createBookingForm(
            @RequestParam(value = "date", required = false) String date,
            @RequestParam(value = "roomId", required = false) String roomId,
            Model model) {
        try {
            Booking booking = Booking.createEmpty();
            if (roomId != null && !roomId.isEmpty()) {
                Room room = roomService.getById(roomId);
                if (room == null) {
                    throw new IllegalArgumentException("Room not found for ID: " + roomId);
                }
                booking.setRoom(room);
            }
            if (date != null && !date.isEmpty()) {
                booking.setStartDate(date);
                // Устанавливаем endDate на следующий день после startDate
                LocalDate start = LocalDate.parse(date);
                booking.setEndDate(start.plusDays(1).toString());
            }
            model.addAttribute("method", "create");
            model.addAttribute("booking", booking);
            model.addAttribute("rooms", roomService.getAll());
            return "edit-booking";
        } catch (Exception e) {
            return error(model, e.getMessage() != null ? e.getMessage() : "An unexpected error occurred.", null);
        }
    }

    @PostMapping("/create-booking")
    public Object createBooking(
            @RequestParam("date") String date,
            @RequestParam("roomId") String roomId,
            @RequestParam("customerName") String customerName,
            @RequestParam("description") String description,
            @RequestParam(value = "customerStreet", required = false) String customerStreet,
            @RequestParam(value = "customerHouseNumber", required = false) String customerHouseNumber,
            @RequestParam(value = "customerPostalCode", required = false) String customerPostalCode,
            @RequestParam(value = "customerCity", required = false) String customerCity,
            @RequestParam(value = "customerCountry", required = false) String customerCountry,
            jakarta.servlet.http.HttpServletRequest request,
            Model model) {
        try {
            Room room = roomService.getById(roomId);
            if (room == null) {
                throw new IllegalArgumentException("Room not found for ID: " + roomId);
            }
            if (customerName == null || customerName.trim().isEmpty()) {
                throw new IllegalArgumentException("Kundenname darf nicht leer sein.");
            }
            if (date == null || date.trim().isEmpty()) {
                throw new IllegalArgumentException("Anfangсdatum darf nicht leer sein.");
            }
            Booking booking = new Booking();
            booking.setStartDate(date);
            booking.setEndDate(date); // Assuming single-day booking for simplicity
            booking.setCustomerName(customerName != null ? customerName : "");
            booking.setDescription(description);
            booking.setRoom(room);
            booking.setStatus("booked");
            booking.setCustomerStreet(customerStreet != null ? customerStreet : "");
            booking.setCustomerHouseNumber(customerHouseNumber != null ? customerHouseNumber : "");
            booking.setCustomerPostalCode(customerPostalCode != null ? customerPostalCode : "");
            booking.setCustomerCity(customerCity != null ? customerCity : "");
            booking.setCustomerCountry(customerCountry != null ? customerCountry : "");
            bookingService.add(booking);

            // Если AJAX-запрос, возвращаем JSON с bookingId
            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                Map<String, String> result = new HashMap<>();
                result.put("bookingId", booking.getID());
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(result);
            }

            // Обычный редирект для обычных форм
            return "redirect:/booking/" + booking.getID();
        } catch (Exception e) {
            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("error", e.getMessage() != null ? e.getMessage() : "Fehler beim Speichern der Buchung"));
            }
            return error(model, e.getMessage() != null ? e.getMessage() : "Fehler при сохранении бронирования", null);
        }
    }

    @GetMapping("/guests/search")
    @ResponseBody
    public List<Guest> searchGuests(@RequestParam("query") String query) {
        return guestService.findByName(query);
    }

    @GetMapping("/calculate-bill")
    @ResponseBody
    public Map<String, Object> calculateBill(
            @RequestParam(value = "roomId", required = false) String roomId,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "guestCount", required = false) Integer guestCount,
            @RequestParam(value = "guests", required = false) List<String> guestDatesOfBirth,
            @RequestParam(value = "dogs", required = false, defaultValue = "0") int dogs,
            @RequestParam(value = "bookingId", required = false) String bookingId,
            @RequestParam(value = "includeBreakfast", required = false, defaultValue = "false") boolean includeBreakfast,
            @RequestParam(value = "prepayment", required = false, defaultValue = "0") double prepayment,
            @RequestParam(value = "roomOrders", required = false) List<String> roomOrdersParam
    ) {
        // Вместо дублирования логики, вызываем roomPricingService.calculateBill
        com.example.demo.classes.Room room = roomService.getById(roomId);
        List<com.example.demo.classes.Guest> guests = new ArrayList<>();
        if (guestDatesOfBirth != null) {
            for (String dob : guestDatesOfBirth) {
                com.example.demo.classes.Guest g = new com.example.demo.classes.Guest();
                g.setDateOfBirth(dob);
                guests.add(g);
            }
        }
        java.time.LocalDate start = java.time.LocalDate.parse(startDate);
        java.time.LocalDate end = java.time.LocalDate.parse(endDate);
        List<com.example.demo.classes.RoomOrder> roomOrders = new ArrayList<>();
        // ...parse roomOrdersParam if needed...
        return roomPricingService.calculateBill(
            room,
            start,
            end,
            guests,
            dogs,
            includeBreakfast,
            prepayment,
            roomOrders
        );
    }

    @GetMapping("/booking/{id}/bill")
    public String viewBill(@PathVariable("id") String id, Model model) {
        try {
            Booking booking = bookingService.getById(id);
            if (booking == null) {
                throw new IllegalArgumentException("Booking not found for ID: " + id);
            }

            // Формируем адрес для отображения
            List<String> customerAddressLines = new ArrayList<>();
            if (booking.getCustomerStreet() != null && !booking.getCustomerStreet().isEmpty()) {
                String line1 = (booking.getCustomerStreet() != null ? booking.getCustomerStreet() : "") +
                               (booking.getCustomerHouseNumber() != null && !booking.getCustomerHouseNumber().isEmpty() ? " " + booking.getCustomerHouseNumber() : "");
                customerAddressLines.add(line1.trim());
            }
            if (booking.getCustomerPostalCode() != null && booking.getCustomerCity() != null &&
                !booking.getCustomerPostalCode().isEmpty() && !booking.getCustomerCity().isEmpty()) {
                customerAddressLines.add((booking.getCustomerPostalCode() + " " + booking.getCustomerCity()).trim());
            }
            if (booking.getCustomerCountry() != null && !booking.getCustomerCountry().isEmpty()) {
                customerAddressLines.add(booking.getCustomerCountry());
            }

            // Форматируем даты в формате "ДД.мм.ГГГГ"
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            String formattedStartDate = LocalDate.parse(booking.getStartDate()).format(formatter);
            String formattedEndDate = LocalDate.parse(booking.getEndDate()).format(formatter);

            // Добавляем текущую дату в модель
            String currentDate = LocalDate.now().format(formatter);
            model.addAttribute("currentDate", currentDate);
            model.addAttribute("formattedStartDate", formattedStartDate);
            model.addAttribute("formattedEndDate", formattedEndDate);
            model.addAttribute("customerAddressLines", customerAddressLines);

            model.addAttribute("booking", booking);
            model.addAttribute("bill", calculateBill(
                booking.getRoom().getID(),
                booking.getStartDate(),
                booking.getEndDate(),
                null,
                booking.getGuests().stream().map(Guest::getDateOfBirth).toList(),
                booking.getDogs(),
                id,
                booking.isIncludeBreakfast(),
                0.0,
                null // Pass null for roomOrdersParam
            ));
            return "bill";
        } catch (Exception e) {
            System.err.println("Error in viewBill: " + e.getMessage());
            e.printStackTrace();
            return error(model, e.getMessage() != null ? e.getMessage() : "An unexpected error occurred.", e.toString());
        }
    }

    @GetMapping("/bookings-list")
    public String bookingsList(
            @RequestParam(value = "q", required = false) String q,
            Model model) {
        // Убираем дубликаты по ID
        List<Booking> all = bookingService.getAll();
        Map<String, Booking> unique = new LinkedHashMap<>();
        for (Booking b : all) {
            if (b.getID() != null && !unique.containsKey(b.getID())) {
                unique.put(b.getID(), b);
            }
        }
        List<Booking> filtered;
        if (q != null && !q.trim().isEmpty()) {
            String query = q.trim().toLowerCase();
            filtered = unique.values().stream().filter(b ->
                (b.getCustomerName() != null && b.getCustomerName().toLowerCase().contains(query)) ||
                (b.getRoom() != null && b.getRoom().getName() != null && b.getRoom().getName().toLowerCase().contains(query)) ||
                (b.getStartDate() != null && b.getStartDate().contains(query)) ||
                (b.getEndDate() != null && b.getEndDate().contains(query)) ||
                (b.getDescription() != null && b.getDescription().toLowerCase().contains(query))
            ).toList();
        } else {
            filtered = new ArrayList<>(unique.values());
        }
        model.addAttribute("bookings", filtered);
        model.addAttribute("q", q);
        return "bookings-list";
    }
}
