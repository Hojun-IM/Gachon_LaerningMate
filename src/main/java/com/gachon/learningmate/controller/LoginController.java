package com.gachon.learningmate.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error, @RequestParam(value = "message", required = false) String message, Model model) {
        // 오류 메시지를 모델에 추가
        if (error != null) {
            model.addAttribute("error_message", message);
        }
        return "login";
    }

}
