package com.gachon.learningmate.controller;

import com.gachon.learningmate.data.dto.RegisterDto;
import com.gachon.learningmate.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@Controller
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterFrom() { return "register"; }

    @PostMapping("/register")
    public String doRegister(@Valid RegisterDto registerDto, Errors errors, Model model) {

        if (errors.hasErrors()) {
            model.addAttribute("registerDto", registerDto);
            // 유효성 검사를 통과 못한 필드와 메시지 핸들링
            Map<String, String> validatorResult = authService.validateHandling(errors);
            for (String key : validatorResult.keySet()) {
                model.addAttribute(key, validatorResult.get(key));
            }
            return "register";
        }

        // 회원가입 로직
        authService.register(registerDto);

        // 회원가입 성공 후, 로그인 화면으로 redirect
        return "redirect:/login";
    }
}