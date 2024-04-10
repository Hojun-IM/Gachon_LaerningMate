package com.gachon.learningmate.controller;

import com.gachon.learningmate.data.dto.RegisterDto;
import com.gachon.learningmate.service.AuthService;
import com.gachon.learningmate.service.MailService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class AuthController {

    private final AuthService authService;
    private final MailService mailService;

    @Autowired
    public AuthController(AuthService authService, MailService mailService) {
        this.authService = authService;
        this.mailService = mailService;
    }

    // 로그인 화면 출력
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    // 회원가입 화면 출력
    @GetMapping("/register")
    public String showRegisterFrom() { return "register"; }

    // 회원가입 입력 정보 전송
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

    // 이메일 전송
    @PostMapping("/register/send-verification")
    public String sendVerificationEmail(@RequestParam String email, Model model, RegisterDto registerDto) {
        model.addAttribute("registerDto", registerDto);

        try {
            authService.sendVerificationCode(email);
            model.addAttribute("message", "인증 코드가 이메일로 전송되었습니다.");
        } catch (Exception e) {
            model.addAttribute("error", "인증 코드 전송에 실패했습니다: " + e.getMessage());
        }
        return "register";
    }

    // 이메일 인증
    @PostMapping("/register/verify-code")
    public String verifyEmailCode(@RequestParam String email, @RequestParam String verificationCode, Model model, RegisterDto registerDto) {
        model.addAttribute("registerDto", registerDto);

        try {
            authService.verifyCode(email, verificationCode);
            model.addAttribute("emailVerified", true);
            model.addAttribute("message", "이메일이 성공적으로 인증되었습니다.");
        } catch (Exception e) {
            model.addAttribute("error", "인증 코드가 잘못되었습니다: " + e.getMessage());
        }
        return "register";
    }
}