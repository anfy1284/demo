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

    public BookingController(RoomService roomService, BookingService bookingService, GuestService guestService, RoomPricingService roomPricingService) {
        this.roomService = roomService;
        this.bookingService = bookingService;
        this.guestService = guestService;
        this.roomPricingService = roomPricingService;
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
    public String updateBooking(
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
            return "redirect:/bookings";
        } catch (Exception e) {
            return error(model, e.getMessage() != null ? e.getMessage() : "An unexpected error occurred.", null);
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
            if (booking == null) { // Reintroduce null check
                throw new IllegalArgumentException("Failed to create a new booking instance.");
            }
            booking.setRoom(room);
            booking.setStartDate(date);
            // Устанавливаем endDate на следующий день после startDate
            LocalDate start = LocalDate.parse(date);
            booking.setEndDate(start.plusDays(1).toString());

            // Calculate bill for the initial state
            RoomPricing roomPricing = roomPricingService.getRoomPricing(roomId);
            if (roomPricing != null) {
                Double dailyPrice = roomPricing.getPrice(start, 1); // Default guest count = 1
                if (dailyPrice != null) {
                    double totalPrice = dailyPrice;
                    Map<String, Object> bill = new HashMap<>();
                    bill.put("accommodationPrice", String.format("%.2f", dailyPrice));
                    bill.put("totalPrice", String.format("%.2f", totalPrice));
                    model.addAttribute("bill", bill);
                }
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
    public String createBooking(
            @RequestParam("date") String date,
            @RequestParam("roomId") String roomId,
            @RequestParam("customerName") String customerName,
            @RequestParam("description") String description,
            // @RequestParam(value = "customerAddress", required = false) String customerAddress, // Удалить
            @RequestParam(value = "customerStreet", required = false) String customerStreet,
            @RequestParam(value = "customerHouseNumber", required = false) String customerHouseNumber,
            @RequestParam(value = "customerPostalCode", required = false) String customerPostalCode,
            @RequestParam(value = "customerCity", required = false) String customerCity,
            @RequestParam(value = "customerCountry", required = false) String customerCountry,
            Model model) {
        try {
            Room room = roomService.getById(roomId);
            if (room == null) {
                throw new IllegalArgumentException("Room not found for ID: " + roomId);
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
            return "redirect:/booking/" + booking.getID();
        } catch (Exception e) {
            return error(model, e.getMessage() != null ? e.getMessage() : "An unexpected error occurred.", null);
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
        Map<String, Object> billSections = new HashMap<>();
        List<Map<String, Object>> billItemsRaw = new ArrayList<>();
        double totalPrice = 0.0;
        double totalKurbeitrag = 0.0;
        double totalRoomOrders = 0.0;

        try {
            if (bookingId != null && prepayment == 0.0) {
                Booking booking = bookingService.getById(bookingId);
                if (booking != null) {
                    prepayment = booking.getPrepayment();
                }
            }

            if (bookingId != null) {
                Booking booking = bookingService.getById(bookingId);
                if (booking == null) {
                    throw new IllegalArgumentException("Booking not found for ID: " + bookingId);
                }
                roomId = booking.getRoom().getID();
                startDate = booking.getStartDate();
                endDate = booking.getEndDate();
                guestDatesOfBirth = booking.getGuests().stream()
                        .map(Guest::getDateOfBirth)
                        .toList();
                dogs = booking.getDogs();
            }

            if (roomId == null || startDate == null || endDate == null) {
                throw new IllegalArgumentException("Missing required parameters for bill calculation.");
            }

            RoomPricing roomPricing = roomPricingService.getRoomPricing(roomId);
            if (roomPricing == null) {
                throw new IllegalArgumentException("Pricing not found for room ID: " + roomId);
            }

            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            long days = start.datesUntil(end.plusDays(1)).count() - 1;

            double accommodationPrice = 0.0;
            double priceForChildren3To5 = roomPricing.getPriceForChildren3To5();
            double priceForChildrenUnder3 = roomPricing.getPriceForChildrenUnder3();
            LocalDate today = LocalDate.now();
            int adultGuestCount = 0;
            int children3To5Count = 0;
            int childrenUnder3Count = 0;
            int children6To15Count = 0;

            Room room = roomService.getById(roomId);
            String roomName = "";
            if (room != null) {
                roomName = room.getName();
            }

            if (guestDatesOfBirth != null) {
                for (String dob : guestDatesOfBirth) {
                    LocalDate birthDate = LocalDate.parse(dob);
                    int age = today.getYear() - birthDate.getYear();
                    if (today.isBefore(birthDate.plusYears(age))) {
                        age--;
                    }
                    if (age <= 2) {
                        childrenUnder3Count++;
                    } else if (age >= 3 && age <= 5) {
                        children3To5Count++;
                    } else if (age >= 6 && age <= 15) {
                        children6To15Count++;
                    } else if (age >= 16) {
                        adultGuestCount++;
                    }
                }
            }

            Double dailyPriceForAdults = roomPricing.getPrice(start, adultGuestCount + children6To15Count);
            if (dailyPriceForAdults == null) {
                throw new IllegalArgumentException("No pricing available for the given date and adult guest count.");
            }
            accommodationPrice += dailyPriceForAdults * days;

            double children3To5Price = children3To5Count * priceForChildren3To5 * days;
            double dogFee = dogs * roomPricing.getDogFeePerNight() * days;

            double kurbeitragUnder6 = childrenUnder3Count * roomPricing.getKurbeitragUnder6() * days;
            double kurbeitrag6To15 = children6To15Count * roomPricing.getKurbeitrag6To15() * days;
            double kurbeitrag16AndOlder = adultGuestCount * roomPricing.getKurbeitrag16AndOlder() * days;
            totalKurbeitrag = kurbeitragUnder6 + kurbeitrag6To15 + kurbeitrag16AndOlder;

            // // Курортный сбор
            // if (kurbeitragUnder6 > 0) {
            //     double gross = kurbeitragUnder6;
            //     double net = gross;
            //     double tax = 0.0;
            //     billItemsRaw.add(Map.of(
            //         "key", "kurbeitragUnder6",
            //         "label", "Kurabgabe 0-5 Jahre",
            //         "value", String.format("%.2f €", gross),
            //         "net", String.format("%.2f €", net),
            //         "tax", String.format("%.2f €", tax),
            //         "taxRate", 0
            //     ));
            // }
            // if (kurbeitrag6To15 > 0) {
            //     double gross = kurbeitrag6To15;
            //     double net = gross;
            //     double tax = 0.0;
            //     billItemsRaw.add(Map.of(
            //         "key", "kurbeitrag6To15",
            //         "label", "Kurabgabe 6-15 Jahre",
            //         "value", String.format("%.2f €", gross),
            //         "net", String.format("%.2f €", net),
            //         "tax", String.format("%.2f €", tax),
            //         "taxRate", 0
            //     ));
            // }
            // if (kurbeitrag16AndOlder > 0) {
            //     double gross = kurbeitrag16AndOlder;
            //     double net = gross;
            //     double tax = 0.0;
            //     billItemsRaw.add(Map.of(
            //         "key", "kurbeitrag16AndOlder",
            //         "label", "Kurabgabe 16+ Jahre",
            //         "value", String.format("%.2f €", gross),
            //         "net", String.format("%.2f €", net),
            //         "tax", String.format("%.2f €", tax),
            //         "taxRate", 0
            //     ));
            // }
            // Удалено добавление Kurabgabe Gesamt
            // if (totalKurbeitrag > 0) {
            //     double gross = totalKurbeitrag;
            //     double net = gross;
            //     double tax = 0.0;
            //     billItemsRaw.add(Map.of(
            //         "key", "totalKurbeitrag",
            //         "label", "Kurabgabe Gesamt",
            //         "value", String.format("%.2f €", gross),
            //         "net", String.format("%.2f €", net),
            //         "tax", String.format("%.2f €", tax),
            //         "taxRate", 0
            //     ));
            // }

            // --- Сбор позиций по категориям ---
            List<Map<String, Object>> uebernachtungItems = new ArrayList<>();
            List<Map<String, Object>> breakfastItems = new ArrayList<>();
            List<Map<String, Object>> kurbeitragItems = new ArrayList<>();
            List<Map<String, Object>> otherItems = new ArrayList<>();

            // --- Основные услуги (ночевки) ---
            if (accommodationPrice > 0) {
                double gross = accommodationPrice;
                double net = gross / 1.07;
                double tax = gross - net;
                // Формируем строку с датами и названием номера
                String label = "";
                if (days > 1) {
                    label = days + " Übernachtungen vom " + start.format(DateTimeFormatter.ofPattern("dd.MM.yy")) +
                            " bis zum " + end.format(DateTimeFormatter.ofPattern("dd.MM.yy")) +
                            " " + roomName;
                } else {
                    label = days + " Übernachtung аm " + start.format(DateTimeFormatter.ofPattern("dd.MM.yy")) +
                            " " + roomName;
                }
                uebernachtungItems.add(Map.of(
                    "key", "accommodationPrice",
                    "label", label,
                    "value", String.format("%.2f €", gross),
                    "net", String.format("%.2f €", net),
                    "tax", String.format("%.2f €", tax),
                    "taxRate", 7
                ));
            }
            if (children3To5Price > 0) {
                double gross = children3To5Price;
                double net = gross / 1.07;
                double tax = gross - net;
                uebernachtungItems.add(Map.of(
                    "key", "children3To5Price",
                    "label", "Kinderpreis (3-5 Jahre)",
                    "value", String.format("%.2f €", gross),
                    "net", String.format("%.2f €", net),
                    "tax", String.format("%.2f €", tax),
                    "taxRate", 7
                ));
            }

            // --- Завтраки ---
            if (includeBreakfast) {
                double breakfastPriceUnder3 = childrenUnder3Count * roomPricing.getBreakfastPriceUnder3() * days;
                double breakfastPrice3To5 = children3To5Count * roomPricing.getBreakfastPrice3To5() * days;
                double breakfastPrice6To13 = children6To15Count * roomPricing.getBreakfastPrice6To13() * days;
                double breakfastPrice14AndOlder = adultGuestCount * roomPricing.getBreakfastPrice14AndOlder() * days;

                if (breakfastPriceUnder3 > 0) {
                    double gross = breakfastPriceUnder3;
                    double net = gross / 1.19;
                    double tax = gross - net;
                    breakfastItems.add(Map.of(
                        "key", "breakfastPriceUnder3",
                        "label", "Frühstück (0-2 Jahre)",
                        "value", String.format("%.2f €", gross),
                        "net", String.format("%.2f €", net),
                        "tax", String.format("%.2f €", tax),
                        "taxRate", 19
                    ));
                }
                if (breakfastPrice3To5 > 0) {
                    double gross = breakfastPrice3To5;
                    double net = gross / 1.19;
                    double tax = gross - net;
                    breakfastItems.add(Map.of(
                        "key", "breakfastPrice3To5",
                        "label", "Frühstück (3-5 Jahre)",
                        "value", String.format("%.2f €", gross),
                        "net", String.format("%.2f €", net),
                        "tax", String.format("%.2f €", tax),
                        "taxRate", 19
                    ));
                }
                if (breakfastPrice6To13 > 0) {
                    double gross = breakfastPrice6To13;
                    double net = gross / 1.19;
                    double tax = gross - net;
                    breakfastItems.add(Map.of(
                        "key", "breakfastPrice6To13",
                        "label", "Frühstück (6-13 Jahre)",
                        "value", String.format("%.2f €", gross),
                        "net", String.format("%.2f €", net),
                        "tax", String.format("%.2f €", tax),
                        "taxRate", 19
                    ));
                }
                if (breakfastPrice14AndOlder > 0) {
                    double gross = breakfastPrice14AndOlder;
                    double net = gross / 1.19;
                    double tax = gross - net;
                    breakfastItems.add(Map.of(
                        "key", "breakfastPrice14AndOlder",
                        "label", "Frühstück (14+ Jahre)",
                        "value", String.format("%.2f €", gross),
                        "net", String.format("%.2f €", net),
                        "tax", String.format("%.2f €", tax),
                        "taxRate", 19
                    ));
                }
                totalPrice += breakfastPriceUnder3 + breakfastPrice3To5 + breakfastPrice6To13 + breakfastPrice14AndOlder;
            }

            // --- Курортные сборы ---
            if (kurbeitragUnder6 > 0) {
                double gross = kurbeitragUnder6;
                double net = gross; // не облагается
                double tax = 0.0;
                kurbeitragItems.add(Map.of(
                    "key", "kurbeitragUnder6",
                    "label", "Kurabgabe 0-5 Jahre",
                    "value", String.format("%.2f €", gross),
                    "net", String.format("%.2f €", net),
                    "tax", String.format("%.2f €", tax),
                    "taxRate", 0
                ));
            }
            if (kurbeitrag6To15 > 0) {
                double gross = kurbeitrag6To15;
                double net = gross;
                double tax = 0.0;
                kurbeitragItems.add(Map.of(
                    "key", "kurbeitrag6To15",
                    "label", "Kurabgabe 6-15 Jahre",
                    "value", String.format("%.2f €", gross),
                    "net", String.format("%.2f €", net),
                    "tax", String.format("%.2f €", tax),
                    "taxRate", 0
                ));
            }
            if (kurbeitrag16AndOlder > 0) {
                double gross = kurbeitrag16AndOlder;
                double net = gross;
                double tax = 0.0;
                kurbeitragItems.add(Map.of(
                    "key", "kurbeitrag16AndOlder",
                    "label", "Kurabgabe 16+ Jahre",
                    "value", String.format("%.2f €", gross),
                    "net", String.format("%.2f €", net),
                    "tax", String.format("%.2f €", tax),
                    "taxRate", 0
                ));
            }
            // Удалено добавление Kurabgabe Gesamt в kurbeitragItems
            // if (totalKurbeitrag > 0) {
            //     double gross = totalKurbeitrag;
            //     double net = gross;
            //     double tax = 0.0;
            //     kurbeitragItems.add(Map.of(
            //         "key", "totalKurbeitrag",
            //         "label", "Kurabgabe Gesamt",
            //         "value", String.format("%.2f €", gross),
            //         "net", String.format("%.2f €", net),
            //         "tax", String.format("%.2f €", tax),
            //         "taxRate", 0
            //     ));
            // }

            // --- Прочие позиции ---
            if (dogFee > 0) {
                double gross = dogFee;
                double net = gross / 1.07;
                double tax = gross - net;
                otherItems.add(Map.of(
                    "key", "dogFee",
                    "label", "Gebühr für den Hund",
                    "value", String.format("%.2f €", gross),
                    "net", String.format("%.2f €", net),
                    "tax", String.format("%.2f €", tax),
                    "taxRate", 7
                ));
            }

            if (days <= 3) {
                double gross = roomPricing.getFinalCleaningFeeShortStay();
                double net = gross / 1.19;
                double tax = gross - net;
                otherItems.add(Map.of(
                    "key", "finalCleaningFee",
                    "label", "Endreinigung (Kurzaufenthalt)",
                    "value", String.format("%.2f €", gross),
                    "net", String.format("%.2f €", net),
                    "tax", String.format("%.2f €", tax),
                    "taxRate", 19
                ));
                totalPrice += gross;
            }

            // Bestellungen im Zimmer
            if (bookingId != null) {
                Booking booking = bookingService.getById(bookingId);
                if (booking != null && booking.getRoomOrders() != null) {
                    for (RoomOrder order : booking.getRoomOrders()) {
                        double gross = order.getPreis();
                        double net = gross / 1.19;
                        double tax = gross - net;
                        totalRoomOrders += gross;
                        otherItems.add(Map.of(
                            "key", "roomOrder",
                            "label", order.getBezeichnung(),
                            "value", String.format("%.2f €", gross),
                            "net", String.format("%.2f €", net),
                            "tax", String.format("%.2f €", tax),
                            "taxRate", 19
                        ));
                    }
                }
            } else if (roomOrdersParam != null && !roomOrdersParam.isEmpty()) {
                for (String orderStr : roomOrdersParam) {
                    String[] parts = orderStr.split(":", 2);
                    if (parts.length == 2) {
                        try {
                            double preis = Double.parseDouble(parts[1]);
                            double gross = preis;
                            double net = gross / 1.19;
                            double tax = gross - net;
                            totalRoomOrders += preis;
                            otherItems.add(Map.of(
                                "key", "roomOrder",
                                "label", java.net.URLDecoder.decode(parts[0], java.nio.charset.StandardCharsets.UTF_8),
                                "value", String.format("%.2f €", gross),
                                "net", String.format("%.2f €", net),
                                "tax", String.format("%.2f €", tax),
                                "taxRate", 19
                            ));
                        } catch (NumberFormatException ignored) {}
                    }
                }
            }
            totalPrice += totalRoomOrders;

            // Итоговые строки (Betrag, Anzahlung, Restbetrag)
            List<Map<String, Object>> totalItems = new ArrayList<>();
            {
                // Betrag должен включать все позиции, включая курортный сбор (Kurabgabe)
                double gross = 0.0;
                for (Map<String, Object> item : uebernachtungItems) gross += parseEuro(item.get("value"));
                for (Map<String, Object> item : breakfastItems) gross += parseEuro(item.get("value"));
                for (Map<String, Object> item : kurbeitragItems) gross += parseEuro(item.get("value"));
                for (Map<String, Object> item : otherItems) gross += parseEuro(item.get("value"));
                // Не нужно дополнительно суммировать roomOrder из billItemsRaw, они уже есть в otherItems
                double net = gross;
                double tax = 0.0;
                totalItems.add(Map.of(
                    "key", "totalPrice",
                    "label", "Betrag",
                    "value", String.format("%.2f €", gross),
                    "net", String.format("%.2f €", net),
                    "tax", String.format("%.2f €", tax),
                    "taxRate", 0
                ));
            }
            {
                double gross = prepayment;
                double net = gross;
                double tax = 0.0;
                totalItems.add(Map.of(
                    "key", "prepayment",
                    "label", "Anzahlung",
                    "value", String.format("%.2f €", gross),
                    "net", String.format("%.2f €", net),
                    "tax", String.format("%.2f €", tax),
                    "taxRate", 0
                ));
            }
            {
                // Restbetrag = Betrag - Anzahlung
                double betrag = 0.0;
                for (Map<String, Object> item : uebernachtungItems) betrag += parseEuro(item.get("value"));
                for (Map<String, Object> item : breakfastItems) betrag += parseEuro(item.get("value"));
                for (Map<String, Object> item : kurbeitragItems) betrag += parseEuro(item.get("value"));
                for (Map<String, Object> item : otherItems) betrag += parseEuro(item.get("value"));
                // Не нужно дополнительно суммировать roomOrder из billItemsRaw, они уже есть в otherItems
                double gross = betrag - prepayment;
                double net = gross;
                double tax = 0.0;
                totalItems.add(Map.of(
                    "key", "totalSum",
                    "label", "Restbetrag",
                    "value", String.format("%.2f €", gross),
                    "net", String.format("%.2f €", net),
                    "tax", String.format("%.2f €", tax),
                    "taxRate", 0
                ));
            }

            // --- Собираем итоговый список в нужном порядке ---
            billItemsRaw.addAll(uebernachtungItems);
            billItemsRaw.addAll(breakfastItems);
            billItemsRaw.addAll(otherItems);
            billItemsRaw.addAll(kurbeitragItems);
            billItemsRaw.addAll(totalItems);

            // Добавляем отдельный раздел для курортного сбора (Kurabgabe)
            //billSections.put("kurbeitrag", kurbeitragItems);

            // --- Суммирование одинаковых позиций по категориям ---
            billSections.put("main", mergeBillItemsByLabelAndTaxRate(billItemsRaw));
            // billSections.put("main", billItemsRaw); // Без объединения одинаковых позиций

            // Корректный расчет налогов по каждой ставке
            double tax7 = 0.0;
            double tax19 = 0.0;
            double tax0 = 0.0;
            for (Map<String, Object> item : billItemsRaw) {
                Object taxRateObj = item.get("taxRate");
                double tax = parseEuro(item.get("tax"));
                if (taxRateObj != null) {
                    int rate = 0;
                    try { rate = Integer.parseInt(taxRateObj.toString()); } catch (Exception ignore) {}
                    if (rate == 7) tax7 += tax;
                    else if (rate == 19) tax19 += tax;
                    else tax0 += tax;
                }
            }
            List<Map<String, Object>> mwstList = new ArrayList<>();
            // if (tax7 > 0.005) mwstList.add(Map.of("label", "MwSt. 7%", "value", String.format("%.2f €", tax7)));
            // if (tax19 > 0.005) mwstList.add(Map.of("label", "MwSt. 19%", "value", String.format("%.2f €", tax19)));
            // if (tax0 > 0.005) mwstList.add(Map.of("label", "MwSt. 0%", "value", String.format("%.2f €", tax0)));
            // Добавляем общую сумму налогов
            double mwstSum = tax7 + tax19 + tax0;
            mwstList.add(Map.of("label", "Gesamte MwSt.", "value", String.format("%.2f €", mwstSum)));
            billSections.put("mwst", mwstList);

        } catch (Exception e) {
            billSections.put("main", List.of(Map.of(
                "key", "error",
                "label", "Fehler",
                "value", e.getMessage(),
                "net", "0.00 €",
                "tax", "0.00 €",
                "taxRate", 19
            )));
            billSections.put("mwst", "0.00 €");
        }
        return billSections;
    }

    // --- Новый метод для суммирования одинаковых позиций ---
    private List<Map<String, Object>> mergeBillItemsByLabelAndTaxRate(List<Map<String, Object>> items) {
        Map<String, Map<String, Object>> merged = new LinkedHashMap<>();
        for (Map<String, Object> item : items) {
            String label = (String) item.get("label");
            Object taxRateObj = item.get("taxRate");
            String key = label + "|" + (taxRateObj != null ? taxRateObj.toString() : "null");
            double value = parseEuro(item.get("value"));
            double net = parseEuro(item.get("net"));
            double tax = parseEuro(item.get("tax"));
            if (merged.containsKey(key)) {
                Map<String, Object> prev = merged.get(key);
                prev.put("value", String.format("%.2f €", parseEuro(prev.get("value")) + value));
                prev.put("net", String.format("%.2f €", parseEuro(prev.get("net")) + net));
                prev.put("tax", String.format("%.2f €", parseEuro(prev.get("tax")) + tax));
            } else {
                merged.put(key, new LinkedHashMap<>(item));
            }
        }
        return new ArrayList<>(merged.values());
    }

    private double parseEuro(Object euroString) {
        if (euroString == null) return 0.0;
        String str = euroString.toString().replace("€", "").replace(" ", "").replace(",", ".").trim();
        try {
            return Double.parseDouble(str);
        } catch (Exception e) {
            return 0.0;
        }
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
}
