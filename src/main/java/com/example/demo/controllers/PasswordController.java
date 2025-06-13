package com.example.demo.controllers;

import com.example.demo.SecurityConfig;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class PasswordController {

    private final InMemoryUserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;

    public PasswordController(InMemoryUserDetailsManager userDetailsManager, PasswordEncoder passwordEncoder) {
        this.userDetailsManager = userDetailsManager;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/post-login")
    public String postLogin(Authentication authentication) {
        String username = authentication.getName();
        if (SecurityConfig.mustChangePassword(username)) {
            return "redirect:/change-password";
        }
        return "redirect:/";
    }

    @GetMapping("/change-password")
    public String changePasswordForm() {
        return "change-password";
    }

    @PostMapping("/do-change-password")
    public String doChangePassword(@RequestParam String oldPassword,
                                   @RequestParam String newPassword,
                                   @RequestParam String confirmPassword,
                                   Authentication authentication,
                                   Model model) {
        String username = authentication.getName();
        UserDetails user = userDetailsManager.loadUserByUsername(username);

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            model.addAttribute("error", "Altes Passwort ist falsch.");
            return "change-password";
        }
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Die Passwörter stimmen nicht überein.");
            return "change-password";
        }
        if (newPassword.length() < 6) {
            model.addAttribute("error", "Das neue Passwort muss mindestens 6 Zeichen lang sein.");
            return "change-password";
        }

        // Меняем пароль и сбрасываем флаг mustChangePassword
        String encoded = passwordEncoder.encode(newPassword);
        userDetailsManager.updateUser(
            org.springframework.security.core.userdetails.User.withUserDetails(user)
                .password(encoded)
                .build()
        );
        SecurityConfig.updatePassword(username, encoded);
        SecurityConfig.reloadUsersFromFile(); // <--- обновляем пользователей в памяти после смены пароля
        // Также обновляем пароль в InMemoryUserDetailsManager для текущей сессии
        // (если userDetailsManager не singleton, используйте аналогичный подход как в AdminController)
        return "redirect:/";
    }
}
