package com.gachon.learningmate.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/login")
public class LoginController {

    @GetMapping
    public String showLoginForm(@RequestParam(required = false) String error,
                                @RequestParam(required = false) String message,
                                Model model) {
        if (error != null) {
            model.addAttribute("error_message", message);
        }
        return "login";
    }
}
