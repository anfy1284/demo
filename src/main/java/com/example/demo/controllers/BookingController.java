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

@Controller
@DependsOn("dataInitializer")
public class BookingController {

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

            // Рассчитываем счет для текущей брони
            Map<String, Object> billSectionsRaw = calculateBill(
                booking.getRoom().getID(),
                booking.getStartDate(),
                booking.getEndDate(),
                (Integer) null,
                booking.getGuests().stream().map(Guest::getDateOfBirth).toList(),
                booking.getDogs(),
                id,
                booking.isIncludeBreakfast(),
                0.0 // Adding prepayment as the last argument
            );
            Map<String, List<Map<String, String>>> billSections = new HashMap<>();
            billSectionsRaw.forEach((key, value) -> {
                if (value instanceof List) {
                    billSections.put(key, (List<Map<String, String>>) value);
                }
            });

            // Отладочный вывод для проверки содержимого счета
            System.out.println("Bill sections: " + billSections);

            model.addAttribute("method", "edit");
            model.addAttribute("booking", booking);
            model.addAttribute("rooms", roomService.getAll());
            model.addAttribute("bill", billSections); // Передаем структуру счета в шаблон

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
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "dogs", required = false, defaultValue = "0") int dogs,
            @RequestParam(value = "includeBreakfast", required = false, defaultValue = "false") boolean includeBreakfast,
            @RequestParam(value = "customerAddress", required = false) String customerAddress,
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
            booking.setCustomerName(customerName);
            booking.setStartDate(startDate);
            booking.setEndDate(endDate);
            booking.setDescription(description);
            booking.setDogs(dogs); // Устанавливаем количество собак
            booking.setIncludeBreakfast(includeBreakfast); // Устанавливаем флаг завтраков
            booking.setCustomerAddress(customerAddress); // Устанавливаем адрес клиента
            booking.setPrepayment(prepayment);

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
            if (booking == null) { // Reintroduce null check
                throw new IllegalArgumentException("Failed to create a new booking instance.");
            }
            booking.setRoom(room);
            booking.setStartDate(date);
            booking.setEndDate(date);

            // Calculate bill for the initial state
            RoomPricing roomPricing = roomPricingService.getRoomPricing(roomId);
            if (roomPricing != null) {
                LocalDate start = LocalDate.parse(date);
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
            @RequestParam(value = "customerAddress", required = false) String customerAddress,
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
            booking.setCustomerAddress(customerAddress); // Set customer address
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
            @RequestParam(value = "prepayment", required = false, defaultValue = "0") double prepayment) {
        Map<String, Object> billSections = new HashMap<>();
        List<Map<String, String>> billItems = new ArrayList<>();
        List<Map<String, String>> kurbeitragItems = new ArrayList<>();
        List<Map<String, String>> gesamtItems = new ArrayList<>();
        double totalPrice = 0.0;

        try {
            // Учитываем предоплату из параметра, если она передана
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
                dogs = booking.getDogs(); // Исправлено: добавлен метод getDogs в Booking
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
            long days = start.datesUntil(end.plusDays(1)).count();

            double accommodationPrice = 0.0;
            double priceForChildren3To5 = roomPricing.getPriceForChildren3To5();
            double priceForChildrenUnder3 = roomPricing.getPriceForChildrenUnder3();
            LocalDate today = LocalDate.now();
            int adultGuestCount = 0;
            int children3To5Count = 0;
            int childrenUnder3Count = 0;
            int children6To15Count = 0;

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

            // Рассчитываем стоимость проживания для взрослых
            Double dailyPriceForAdults = roomPricing.getPrice(start, adultGuestCount + children6To15Count);
            if (dailyPriceForAdults == null) {
                throw new IllegalArgumentException("No pricing available for the given date and adult guest count.");
            }
            accommodationPrice += dailyPriceForAdults * days;

            // Рассчитываем стоимость для детей от 3 до 5 лет
            double children3To5Price = children3To5Count * priceForChildren3To5 * days;

            // Рассчитываем стоимость за собак
            double dogFee = dogs * roomPricing.getDogFeePerNight() * days;

            // Рассчитываем курортный сбор
            double kurbeitragUnder6 = childrenUnder3Count * roomPricing.getKurbeitragUnder6() * days;
            double kurbeitrag6To15 = children6To15Count * roomPricing.getKurbeitrag6To15() * days;
            double kurbeitrag16AndOlder = adultGuestCount * roomPricing.getKurbeitrag16AndOlder() * days;
            double totalKurbeitrag = kurbeitragUnder6 + kurbeitrag6To15 + kurbeitrag16AndOlder;

            // Add Kurbeitrag items to a separate list
            if (kurbeitragUnder6 > 0) {
                kurbeitragItems.add(Map.of(
                    "key", "kurbeitragUnder6",
                    "label", "0-5 Jahre",
                    "value", String.format("%.2f €", kurbeitragUnder6)
                ));
            }
            if (kurbeitrag6To15 > 0) {
                kurbeitragItems.add(Map.of(
                    "key", "kurbeitrag6To15",
                    "label", "6-15 Jahre",
                    "value", String.format("%.2f €", kurbeitrag6To15)
                ));
            }
            if (kurbeitrag16AndOlder > 0) {
                kurbeitragItems.add(Map.of(
                    "key", "kurbeitrag16AndOlder",
                    "label", "16+ Jahre",
                    "value", String.format("%.2f €", kurbeitrag16AndOlder)
                ));
            }

            // Add total row for Kurbeitrag
            kurbeitragItems.add(Map.of(
                "key", "totalKurbeitrag",
                "label", "Gesamt",
                "value", String.format("%.2f €", totalKurbeitrag)
            ));

            // Add other bill items to the main list
            if (accommodationPrice > 0) {
                billItems.add(Map.of(
                    "key", "accommodationPrice",
                    "label", "Unterkunftspreis (Erwachsene и Kinder ab 6 Jahren)",
                    "value", String.format("%.2f €", accommodationPrice)
                ));
            }
            if (children3To5Price > 0) {
                billItems.add(Map.of(
                    "key", "children3To5Price",
                    "label", "Kinderpreis (3-5 Jahre)",
                    "value", String.format("%.2f €", children3To5Price)
                ));
            }
            /*
            if (childrenUnder3Count > 0) {
                billItems.add(Map.of(
                    "key", "childrenUnder3Price",
                    "label", "Kinderpreis (0-2 Jahre, kostenlos)",
                    "value", "0.00 €"
                ));
            }
            */
            if (dogFee > 0) {
                billItems.add(Map.of(
                    "key", "dogFee",
                    "label", "Gebühr für den Hund",
                    "value", String.format("%.2f €", dogFee)
                ));
            }

            // Добавляем стоимость финальной уборки для короткого пребывания
            if (days <= 3) {
                double finalCleaningFee = roomPricing.getFinalCleaningFeeShortStay();
                billItems.add(Map.of(
                    "key", "finalCleaningFee",
                    "label", "Endreinigung (Kurzaufenthalt)",
                    "value", String.format("%.2f €", finalCleaningFee)
                ));
                totalPrice += finalCleaningFee;
            }

            // Добавляем стоимость завтраков по категориям
            if (includeBreakfast) {
                double breakfastPriceUnder3 = childrenUnder3Count * roomPricing.getBreakfastPriceUnder3() * days;
                double breakfastPrice3To5 = children3To5Count * roomPricing.getBreakfastPrice3To5() * days;
                double breakfastPrice6To13 = children6To15Count * roomPricing.getBreakfastPrice6To13() * days;
                double breakfastPrice14AndOlder = adultGuestCount * roomPricing.getBreakfastPrice14AndOlder() * days;

                if (breakfastPriceUnder3 > 0) {
                    billItems.add(Map.of(
                        "key", "breakfastPriceUnder3",
                        "label", "Frühstück (0-2 Jahre)",
                        "value", String.format("%.2f €", breakfastPriceUnder3)
                    ));
                }
                if (breakfastPrice3To5 > 0) {
                    billItems.add(Map.of(
                        "key", "breakfastPrice3To5",
                        "label", "Frühstück (3-5 Jahre)",
                        "value", String.format("%.2f €", breakfastPrice3To5)
                    ));
                }
                if (breakfastPrice6To13 > 0) {
                    billItems.add(Map.of(
                        "key", "breakfastPrice6To13",
                        "label", "Frühstück (6-13 Jahre)",
                        "value", String.format("%.2f €", breakfastPrice6To13)
                    ));
                }
                if (breakfastPrice14AndOlder > 0) {
                    billItems.add(Map.of(
                        "key", "breakfastPrice14AndOlder",
                        "label", "Frühstück (14+ Jahre)",
                        "value", String.format("%.2f €", breakfastPrice14AndOlder)
                    ));
                }
                totalPrice += breakfastPriceUnder3 + breakfastPrice3To5 + breakfastPrice6To13 + breakfastPrice14AndOlder;
            }

            // Итоговая строка
            totalPrice += accommodationPrice + children3To5Price + dogFee;

            // Добавляем строку с итоговой суммой
            billItems.add(Map.of(
                "key", "totalPrice",
                "label", "Betrag",
                "value", String.format("%.2f €", totalPrice)
            ));

            // Вычисляем 7% MwSt от итоговой суммы
            double mwst = totalPrice * 0.07;

            // Добавляем строки в раздел "Gesamt"
            gesamtItems.add(Map.of(
                "key", "prepayment",
                "label", "Anzahlung",
                "value", String.format("%.2f €", prepayment)
            ));
            gesamtItems.add(Map.of(
                "key", "totalSum",
                "label", "Restbetrag",
                "value", String.format("%.2f €", totalPrice + totalKurbeitrag - prepayment)
            ));

            // Добавляем все разделы в ответ
            billSections.put("main", billItems);
            billSections.put("kurbeitrag", kurbeitragItems);
            billSections.put("gesamt", gesamtItems);
            billSections.put("mwst", String.format("%.2f €", mwst)); // Добавляем MwSt в ответ

        } catch (Exception e) {
            billItems.add(Map.of(
                "key", "error",
                "label", "Fehler",
                "value", e.getMessage()
            ));
        }
        return billSections;
    }

    @GetMapping("/booking/{id}/bill")
    public String viewBill(@PathVariable("id") String id, Model model) {
        try {
            Booking booking = bookingService.getById(id);
            if (booking == null) {
                throw new IllegalArgumentException("Booking not found for ID: " + id);
            }

            // Форматируем даты в формате "ДД.мм.ГГГГ"
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            String formattedStartDate = LocalDate.parse(booking.getStartDate()).format(formatter);
            String formattedEndDate = LocalDate.parse(booking.getEndDate()).format(formatter);

            // Разделяем адрес клиента на строки
            String[] customerAddressLines = booking.getCustomerAddress().split(",");

            // Добавляем текущую дату в модель
            String currentDate = LocalDate.now().format(formatter);
            model.addAttribute("currentDate", currentDate);
            model.addAttribute("formattedStartDate", formattedStartDate);
            model.addAttribute("formattedEndDate", formattedEndDate);
            model.addAttribute("customerAddressLines", Arrays.asList(customerAddressLines));

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
                0.0
            ));
            return "bill";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage() != null ? e.getMessage() : "An unexpected error occurred.");
            return "error";
        }
    }
}
