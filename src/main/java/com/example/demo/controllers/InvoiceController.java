package com.example.demo.controllers;

import com.example.demo.classes.Booking;
import com.example.demo.services.BookingService;
import com.example.demo.services.RoomService;
import com.example.demo.services.GuestService;
import com.example.demo.services.RoomPricingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/invoice")
public class InvoiceController {

    private final BookingService bookingService;
    private final RoomService roomService;
    private final GuestService guestService;
    private final RoomPricingService roomPricingService;

    public InvoiceController(BookingService bookingService, RoomService roomService, GuestService guestService, RoomPricingService roomPricingService) {
        this.bookingService = bookingService;
        this.roomService = roomService;
        this.guestService = guestService;
        this.roomPricingService = roomPricingService;
    }

    @GetMapping("/new")
    public String newInvoice(
            @RequestParam(value = "add", required = false) String addId,
            @RequestParam(value = "remove", required = false) String removeId,
            HttpSession session,
            Model model
    ) {
        // Сохраняем список выбранных ID в сессии
        List<String> invoiceBookingIdsTmp = (List<String>) session.getAttribute("invoiceBookingIds");
        final List<String> invoiceBookingIds = (invoiceBookingIdsTmp == null) ? new ArrayList<>() : invoiceBookingIdsTmp;
        if (addId != null && !invoiceBookingIds.contains(addId)) invoiceBookingIds.add(addId);
        if (removeId != null) invoiceBookingIds.remove(removeId);
        session.setAttribute("invoiceBookingIds", invoiceBookingIds);

        List<Booking> allBookings = bookingService.getAll();
        List<Booking> invoiceBookings = allBookings.stream()
                .filter(b -> invoiceBookingIds.contains(b.getID()))
                .collect(Collectors.toList());

        // Собираем суммарный счет по всем выбранным броням
        Map<String, Object> bill = InvoiceUtil.calculateInvoiceBill(invoiceBookings, roomPricingService, roomService);

        model.addAttribute("allBookings", allBookings);
        model.addAttribute("invoiceBookings", invoiceBookings);
        model.addAttribute("bill", bill);
        // Добавляем текущую дату для шаблона счета
        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        model.addAttribute("currentDate", currentDate);
        return "invoice";
    }

    @PostMapping("/add")
    public ResponseEntity<String> addBookingToInvoice(@RequestBody Map<String, String> payload, HttpSession session) {
        String bookingId = payload.get("bookingId");
        if (bookingId == null || bookingId.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        List<String> invoiceBookingIds = (List<String>) session.getAttribute("invoiceBookingIds");
        if (invoiceBookingIds == null) {
            invoiceBookingIds = new ArrayList<>();
            session.setAttribute("invoiceBookingIds", invoiceBookingIds);
        }

        if (!invoiceBookingIds.contains(bookingId)) {
            invoiceBookingIds.add(bookingId);
        }

        return ResponseEntity.ok().build();
    }
}
