package com.gachon.learningmate.controller;

import com.gachon.learningmate.data.dto.RegisterDto;
import com.gachon.learningmate.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String showRegisterFrom() {
        return "register";
    }

    @PostMapping("/register")
    public String doRegister(@ModelAttribute @Valid RegisterDto registerDto, BindingResult result, RedirectAttributes redirectAttributes, Model model) {
        // 유효성 검사 실패 시, 회원가입 화면으로 redirect
        if (result.hasErrors()) {
            model.addAttribute("errors", result.getAllErrors());
            model.addAttribute("registerDto", registerDto);
            return "redirect:/register";
        }

        // 회원가입 로직
        authService.register(registerDto);

        // 성공 메세지를 redirect
        redirectAttributes.addFlashAttribute("successMessage", "회원가입이 성공적으로 완료되었습니다");

        // 회원가입 성공 후, 로그인 화면으로 redirect
        return "redirect:/login";
    }
}
