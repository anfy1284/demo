package com.example.demo.controllers;

import org.springframework.ui.Model;

public abstract class BaseErrorController {
    protected String error(Model model, String errorMessage, String errorDetails) {
        model.addAttribute("errorMessage", errorMessage);
        if (errorDetails != null) {
            model.addAttribute("errorDetails", errorDetails);
        }
        return "error";
    }
}
