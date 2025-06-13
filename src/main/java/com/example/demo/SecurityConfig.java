package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class SecurityConfig {

    private static final String USERS_FILE = getDataFilePath("users.dat");
    private static final ConcurrentHashMap<String, Boolean> mustChangePassword = new ConcurrentHashMap<>();
    private static final Map<String, UserRecord> users = new ConcurrentHashMap<>();

    public static class UserRecord implements Serializable {
        public String username;
        public String passwordHash;
        public String[] roles;
        public boolean mustChangePassword;
        public UserRecord(String username, String passwordHash, String[] roles, boolean mustChangePassword) {
            this.username = username;
            this.passwordHash = passwordHash;
            this.roles = roles;
            this.mustChangePassword = mustChangePassword;
        }

        public String getUsername() { return username; }
        public String[] getRoles() { return roles; }
        public boolean isMustChangePassword() { return mustChangePassword; }
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/css/**", "/js/**", "/images/**", "/change-password", "/do-change-password").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN") // <--- добавьте эту строку
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login").permitAll()
                .defaultSuccessUrl("/post-login", true)
            )
            .logout(logout -> logout.permitAll());
        http.csrf(csrf -> csrf.disable());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public InMemoryUserDetailsManager inMemoryUserDetailsManager(PasswordEncoder encoder) {
        loadUsersFromFile();
        if (users.isEmpty()) {
            // Используем нешифрованные пароли для первого входа
            users.put("admin", new UserRecord("admin", "{noop}123456", new String[]{"ADMIN"}, true));
            // saveUsersToFile();
            // --- Почему то так работает а так как выше нет ---
            UserRecord admin = users.get("admin");
            if (admin != null) {
                admin.passwordHash = encoder.encode("123456");
                admin.mustChangePassword = true; // Требуется смена пароля                
            }
            saveUsersToFile();
        }

        mustChangePassword.clear();
        for (UserRecord rec : users.values()) {
            mustChangePassword.put(rec.username, rec.mustChangePassword);
        }
        List<UserDetails> userDetailsList = new ArrayList<>();
        for (UserRecord rec : users.values()) {
            userDetailsList.add(
                User.withUsername(rec.username)
                    .password(rec.passwordHash)
                    .roles(rec.roles)
                    .build()
            );
        }
        return new InMemoryUserDetailsManager(userDetailsList);
    }

    // Методы для смены пароля и обновления mustChangePassword
    public static boolean mustChangePassword(String username) {
        return mustChangePassword.getOrDefault(username, false);
    }
    public static void setPasswordChanged(String username) {
        mustChangePassword.put(username, false);
        UserRecord rec = users.get(username);
        if (rec != null) {
            rec.mustChangePassword = false;
            saveUsersToFile();
        }
    }
    public static void setPasswordMustChange(String username) {
        mustChangePassword.put(username, true);
        UserRecord rec = users.get(username);
        if (rec != null) {
            rec.mustChangePassword = true;
            saveUsersToFile();
        }
    }
    public static void updatePassword(String username, String encodedPassword) {
        UserRecord rec = users.get(username);
        if (rec != null) {
            rec.passwordHash = encodedPassword;
            rec.mustChangePassword = false;
            saveUsersToFile();
        }
    }

    // --- Админские методы для управления пользователями ---
    public static Collection<UserRecord> getAllUsers() {
        return users.values();
    }
    public static boolean userExists(String username) {
        return users.containsKey(username);
    }
    public static void addUser(String username, String encodedPassword, String[] roles, boolean mustChange) {
        users.put(username, new UserRecord(username, encodedPassword, roles, mustChange));
        mustChangePassword.put(username, mustChange);
        saveUsersToFile();
    }
    public static void deleteUser(String username) {
        users.remove(username);
        mustChangePassword.remove(username);
        saveUsersToFile();
    }
    public static void updateRoles(String username, String[] roles) {
        UserRecord rec = users.get(username);
        if (rec != null) {
            rec.roles = roles;
            saveUsersToFile();
        }
    }

    // Сериализация пользователей
    private static void saveUsersToFile() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(USERS_FILE))) {
            out.writeObject(new ArrayList<>(users.values()));
        } catch (Exception e) {
            System.err.println("Failed to save users: " + e.getMessage());
        }
    }
    private static void loadUsersFromFile() {
        File f = new File(USERS_FILE);
        if (!f.exists()) return;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(f))) {
            List<UserRecord> list = (List<UserRecord>) in.readObject();
            users.clear();
            for (UserRecord rec : list) {
                users.put(rec.username, rec);
            }
        } catch (Exception e) {
            System.err.println("Failed to load users: " + e.getMessage());
        }
    }
    public static void reloadUsersFromFile() {
        loadUsersFromFile();
        mustChangePassword.clear();
        for (UserRecord rec : users.values()) {
            mustChangePassword.put(rec.username, rec.mustChangePassword);
        }
    }

    private static String getDataFilePath(String filename) {
        File dataDir = new File("data");
        File dataFile = new File(dataDir, filename);
        if (dataFile.exists() || dataDir.exists()) {
            return dataFile.getAbsolutePath();
        }
        // fallback: текущая директория
        return filename;
    }
}
