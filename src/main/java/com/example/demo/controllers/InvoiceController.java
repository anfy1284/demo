package com.example.demo.controllers;

import com.example.demo.classes.Booking;
import com.example.demo.classes.Invoice;
import com.example.demo.classes.InvoiceItem;
import com.example.demo.services.BookingService;
import com.example.demo.services.InvoiceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/invoices")
public class InvoiceController {
    private final InvoiceService invoiceService;
    private final BookingService bookingService;

    public InvoiceController(InvoiceService invoiceService, BookingService bookingService) {
        this.invoiceService = invoiceService;
        this.bookingService = bookingService;
    }

    @GetMapping({"/create", "/edit/{id}"})
    public String invoiceForm(
            @PathVariable(value = "id", required = false) String id,
            @RequestParam(value = "bookingId", required = false) String bookingId,
            Model model) {
        Invoice invoice;

        if (id != null) {
            invoice = invoiceService.getById(id);
            if (invoice == null) {
                return error(model, "Rechnung nicht gefunden.");
            }
        } else {
            invoice = new Invoice();
            if (bookingId != null && !bookingId.isEmpty()) {
                Booking booking = bookingService.getById(bookingId);
                if (booking == null || booking.getID().isEmpty()) {
                    return error(model, "Buchung nicht gefunden oder ungültig.");
                }
                invoice.getBookings().add(booking);
                model.addAttribute("booking", booking);
            }
        }

        model.addAttribute("invoice", invoice);
        return "invoice"; // Используем invoice.ftlh
    }

    @PostMapping("/invoices/create")
    public String createInvoice(@RequestParam("bookingId") String bookingId, Model model) {
        Booking booking = bookingService.getById(bookingId);
        if (booking == null) {
            return error(model, "Buchung nicht gefunden.");
        }

        // Создаем новый счет
        Invoice invoice = new Invoice();
        invoice.getBookings().add(booking);

        // Заполняем позиции счета на основе данных бронирования
        InvoiceItem accommodationItem = new InvoiceItem(
                "Übernachtung: " + booking.getRoom().getName() + " (" + booking.getStartDate() + " - " + booking.getEndDate() + ")",
                calculateAccommodationPrice(booking)
        );
        invoice.getItems().add(accommodationItem);

        if (booking.getDogs() > 0) {
            InvoiceItem dogFeeItem = new InvoiceItem(
                    "Gebühr für den Hund",
                    calculateDogFee(booking)
            );
            invoice.getItems().add(dogFeeItem);
        }

        if (booking.isIncludeBreakfast()) {
            InvoiceItem breakfastItem = new InvoiceItem(
                    "Frühstück",
                    calculateBreakfastFee(booking)
            );
            invoice.getItems().add(breakfastItem);
        }

        // Рассчитываем общую сумму
        double totalAmount = invoice.getItems().stream().mapToDouble(InvoiceItem::getAmount).sum();
        invoice.setTotalAmount(totalAmount);

        // Сохраняем счет временно
        invoiceService.add(invoice);

        // Передаем данные в модель
        model.addAttribute("invoice", invoice);

        // Удаляем счет, если он не будет сохранен
        model.addAttribute("deleteOnCancel", true);

        return "invoice";
    }

    @PostMapping("/save")
    public String saveInvoice(@RequestParam("invoiceId") String invoiceId, Model model) {
        Invoice invoice = invoiceService.getById(invoiceId);
        if (invoice == null) {
            return error(model, "Invoice not found.");
        }

        // Сохраняем счет окончательно
        invoiceService.saveAllToFile();
        return "redirect:/invoices/" + invoiceId;
    }

    @GetMapping("/cancel")
    public String cancelInvoice(@RequestParam("invoiceId") String invoiceId, Model model) {
        Invoice invoice = invoiceService.getById(invoiceId);
        if (invoice != null) {
            // Удаляем временный счет
            invoiceService.delete(invoiceId);
        }
        return "redirect:/bookings";
    }

    @GetMapping("/{id}")
    public String viewInvoice(@PathVariable("id") String id, Model model) {
        Invoice invoice = invoiceService.getById(id);
        if (invoice == null) {
            return error(model, "Invoice not found.");
        }

        model.addAttribute("invoice", invoice);
        return "invoice";
    }

    private String error(Model model, String errorMessage) {
        model.addAttribute("errorMessage", errorMessage);
        return "error";
    }

    // Вспомогательные методы для расчетов
    private double calculateAccommodationPrice(Booking booking) {
        // Логика расчета стоимости проживания
        return 100.0; // Пример
    }

    private double calculateDogFee(Booking booking) {
        // Логика расчета стоимости за собак
        return booking.getDogs() * 10.0; // Пример
    }

    private double calculateBreakfastFee(Booking booking) {
        // Логика расчета стоимости завтраков
        return booking.getGuests().size() * 15.0; // Пример
    }
}
