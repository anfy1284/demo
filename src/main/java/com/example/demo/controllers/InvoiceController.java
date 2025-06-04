package com.example.demo.controllers;

import com.example.demo.classes.Booking;
import com.example.demo.services.BookingService;
import com.example.demo.services.RoomService;
import com.example.demo.services.GuestService;
import com.example.demo.services.RoomPricingService;
import com.example.demo.services.InvoiceService;
import com.example.demo.classes.Invoice;
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
public class InvoiceController {

    private final BookingService bookingService;
    private final RoomService roomService;
    private final GuestService guestService;
    private final RoomPricingService roomPricingService;
    private final InvoiceService invoiceService;

    public InvoiceController(BookingService bookingService, RoomService roomService, GuestService guestService, RoomPricingService roomPricingService, InvoiceService invoiceService) {
        this.bookingService = bookingService;
        this.roomService = roomService;
        this.guestService = guestService;
        this.roomPricingService = roomPricingService;
        this.invoiceService = invoiceService;
    }

    @GetMapping("/invoice/new")
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
        // Добавляем текущую дату и следующий номер счета
        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String nextInvoiceNumber = invoiceService.getNextInvoiceNumber();
        model.addAttribute("currentDate", currentDate);
        model.addAttribute("nextInvoiceNumber", nextInvoiceNumber);
        return "invoice";
    }

    @PostMapping("/invoice/add")
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

    @PostMapping("/invoice/save")
    @ResponseBody
    public Map<String, Object> saveInvoice(@RequestBody Map<String, Object> payload, HttpSession session) {
        try {
            String date = (String) payload.get("date");
            String number = (String) payload.get("number");
            List<String> bookingIds = (List<String>) payload.get("bookingIds");
            if (date == null || number == null || bookingIds == null || bookingIds.isEmpty()) {
                return Map.of("success", false, "error", "Alle Felder müssen ausgefüllt werden.");
            }
            // Получаем bill для выбранных броней
            List<Booking> bookings = bookingService.getAll().stream()
                .filter(b -> bookingIds.contains(b.getID()))
                .collect(Collectors.toList());
            Map<String, Object> bill = InvoiceUtil.calculateInvoiceBill(bookings, roomPricingService, roomService);
            Invoice invoice = new Invoice(number, date, bookingIds, bill);
            invoiceService.save(invoice);
            return Map.of("success", true);
        } catch (Exception e) {
            return Map.of("success", false, "error", e.getMessage());
        }
    }

    @GetMapping("/invoice/view/{number}")
    public String viewInvoice(@PathVariable("number") String number, Model model) {
        Invoice invoice = invoiceService.getByNumber(number);
        if (invoice == null) {
            model.addAttribute("error", "Rechnung nicht gefunden.");
            return "invoices";
        }
        List<Booking> bookings = bookingService.getAll().stream()
            .filter(b -> invoice.getBookingIds().contains(b.getID()))
            .collect(Collectors.toList());
        model.addAttribute("invoice", invoice);
        model.addAttribute("invoiceBookings", bookings);
        model.addAttribute("bill", invoice.getBill()); // <-- используем сохранённый bill
        model.addAttribute("currentDate", invoice.getDate());
        model.addAttribute("readonly", true); // флаг для режима просмотра
        return "invoice";
    }

    @GetMapping("/invoices")
    public String listInvoices(Model model) {
        List<Invoice> invoices = invoiceService.getAll();
        // Для таблицы: total = bill.main.last().net
        for (Invoice inv : invoices) {
            if (inv.getBill() != null && inv.getBill().get("main") instanceof List main && !main.isEmpty()) {
                Map last = (Map) ((List) main).get(((List) main).size() - 1);
                inv.setBill(inv.getBill()); // just to be sure
                inv.getBill().put("total", last.get("net"));
            }
        }
        model.addAttribute("invoices", invoices);
        model.addAttribute("bookings", bookingService.getAll()); // <-- добавьте эту строку
        return "invoices";
    }

    @PostMapping("/invoice/delete/{number}")
    @ResponseBody
    public void deleteInvoice(@PathVariable("number") String number) {
        invoiceService.delete(number);
    }
}
