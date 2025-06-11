package com.example.demo.controllers;

import com.example.demo.SecurityConfig;
import com.example.demo.SecurityConfig.UserRecord;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')") // <--- обязательно!
public class AdminController {

    private final PasswordEncoder passwordEncoder;

    public AdminController(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
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
        }
        return "redirect:/admin";
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam String username) {
        SecurityConfig.deleteUser(username);
        return "redirect:/admin";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String username, @RequestParam String password) {
        String encoded = password.startsWith("{noop}") ? password : passwordEncoder.encode(password);
        SecurityConfig.updatePassword(username, encoded);
        SecurityConfig.setPasswordMustChange(username);
        return "redirect:/admin";
    }

    @PostMapping("/force-change")
    public String forceChange(@RequestParam String username) {
        SecurityConfig.setPasswordMustChange(username);
        return "redirect:/admin";
    }

    @PostMapping("/edit")
    public String editUser(@RequestParam String username,
                           @RequestParam String[] roles) {
        SecurityConfig.updateRoles(username, roles);
        return "redirect:/admin";
    }
}
