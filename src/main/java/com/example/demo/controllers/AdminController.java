package com.example.demo.controllers;

import com.example.demo.SecurityConfig;
import com.example.demo.SecurityConfig.UserRecord;
import com.example.demo.services.BookingService;
import com.example.demo.services.GuestService;
import com.example.demo.services.InvoiceService;
import com.example.demo.services.RoomPricingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.io.*;
import java.util.*;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')") // <--- обязательно!
public class AdminController {

    private final PasswordEncoder passwordEncoder;
    private final BookingService bookingService;
    private final GuestService guestService;
    private final InvoiceService invoiceService;
    private final RoomPricingService roomPricingService;
    private final InMemoryUserDetailsManager userDetailsManager;

    @Autowired
    public AdminController(PasswordEncoder passwordEncoder,
                          BookingService bookingService,
                          GuestService guestService,
                          InvoiceService invoiceService,
                          RoomPricingService roomPricingService,
                          InMemoryUserDetailsManager userDetailsManager) {
        this.passwordEncoder = passwordEncoder;
        this.bookingService = bookingService;
        this.guestService = guestService;
        this.invoiceService = invoiceService;
        this.roomPricingService = roomPricingService;
        this.userDetailsManager = userDetailsManager;
    }

    @GetMapping
    public String adminPage(Model model) {
        Collection<UserRecord> users = SecurityConfig.getAllUsers();
        model.addAttribute("users", users);
        return "admin";
    }

    @PostMapping("/add")
    public String addUser(@RequestParam String username,
                          @RequestParam String password,
                          @RequestParam(defaultValue = "USER") String role,
                          Model model) {
        if (SecurityConfig.userExists(username)) {
            model.addAttribute("error", "Пользователь уже существует");
        } else {
            String encoded = password.startsWith("{noop}") ? password : passwordEncoder.encode(password);
            SecurityConfig.addUser(username, encoded, new String[]{role}, true);
            SecurityConfig.reloadUsersFromFile();
            // --- обновляем пользователей в InMemoryUserDetailsManager ---
            org.springframework.security.core.userdetails.UserDetails newUser =
                org.springframework.security.core.userdetails.User.withUsername(username)
                    .password(encoded)
                    .roles(role)
                    .build();
            userDetailsManager.createUser(newUser);
        }
        return "redirect:/admin";
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam String username) {
        SecurityConfig.deleteUser(username);
        SecurityConfig.reloadUsersFromFile();
        userDetailsManager.deleteUser(username);
        return "redirect:/admin";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String username, @RequestParam String password) {
        String encoded = password.startsWith("{noop}") ? password : passwordEncoder.encode(password);
        SecurityConfig.updatePassword(username, encoded);
        SecurityConfig.setPasswordMustChange(username);
        SecurityConfig.reloadUsersFromFile();
        org.springframework.security.core.userdetails.UserDetails user =
            userDetailsManager.loadUserByUsername(username);
        org.springframework.security.core.userdetails.UserDetails updated =
            org.springframework.security.core.userdetails.User.withUserDetails(user)
                .password(encoded)
                .build();
        userDetailsManager.updateUser(updated);
        return "redirect:/admin";
    }

    @PostMapping("/force-change")
    public String forceChange(@RequestParam String username) {
        SecurityConfig.setPasswordMustChange(username);
        SecurityConfig.reloadUsersFromFile();
        return "redirect:/admin";
    }

    @PostMapping("/edit")
    public String editUser(@RequestParam String username,
                           @RequestParam String[] roles) {
        SecurityConfig.updateRoles(username, roles);
        SecurityConfig.reloadUsersFromFile();
        org.springframework.security.core.userdetails.UserDetails user =
            userDetailsManager.loadUserByUsername(username);
        org.springframework.security.core.userdetails.UserDetails updated =
            org.springframework.security.core.userdetails.User.withUserDetails(user)
                .roles(roles)
                .build();
        userDetailsManager.updateUser(updated);
        return "redirect:/admin";
    }

    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadFile(@RequestParam String filename) throws IOException {
        File dataDir = new File("/data");
        File file = dataDir.exists() ? new File(dataDir, filename) : new File(filename);
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(file.length())
                .body(resource);
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file,
                             @RequestParam("target") String target,
                             Model model) {
        // if (file.isEmpty()) {
        //     model.addAttribute("error", "Файл не выбран");
        //     return "redirect:/admin";
        // }
        try {
            File dataDir = new File("/data");
            File dest = dataDir.exists() ? new File(dataDir, target) : new File(target);
            // Удаляем старый файл, если он есть
            if (dest.exists()) {
                if (!dest.delete()) {
                    model.addAttribute("error", "Не удалось удалить старый файл: " + dest.getAbsolutePath());
                    return "redirect:/admin";
                }
            }
            // Копируем новый файл
            file.transferTo(dest);
            model.addAttribute("message", "Файл успешно загружен: " + dest.getAbsolutePath());

            // После загрузки файла сразу перечитываем все данные
            if ("users.dat".equals(target)) {
                SecurityConfig.reloadUsersFromFile();
                // обновить пользователей в памяти
                // (если нужно, обновите InMemoryUserDetailsManager аналогично AdminController)
            } else if ("guests.dat".equals(target)) {
                guestService.loadAllFromFile();
            } else if ("bookings.dat".equals(target)) {
                bookingService.loadAllFromFile();
            } else if ("invoices.dat".equals(target)) {
                invoiceService.reloadAllFromFile();
            }
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка загрузки файла: " + e.getMessage());
        }
        return "redirect:/admin";
    }

    @PostMapping("/reload")
    public String reloadAllData() {
        SecurityConfig.reloadUsersFromFile();
        guestService.loadAllFromFile();
        bookingService.loadAllFromFile();
        invoiceService.reloadAllFromFile();
        roomPricingService.reloadAllFromFile();
        return "redirect:/admin";
    }
}
