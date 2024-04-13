package com.gachon.learningmate.controller;

import com.gachon.learningmate.data.dto.RegisterDto;
import com.gachon.learningmate.service.AuthService;
import com.gachon.learningmate.service.MailService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
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

    // 회원가입 화면 출력 (이메일 인증)
    @GetMapping("/registerFirst")
    public String showRegisterFromFirst() { return "registerFirst"; }

    /// 회원가입 화면 출력 (나머지 정보)
    @GetMapping("/registerSecond")
    public String showRegisterFromSecond() { return "registerSecond"; }

    // 회원가입 이메일 정보 전송
    @PostMapping("/registerFirst")
    public String doRegisterFirst(@Valid RegisterDto registerDto, Errors errors, Model model) {

        if (errors.hasErrors()) {
            model.addAttribute("registerDto", registerDto);
            // 유효성 검사를 통과 못한 필드와 메시지 핸들링
            Map<String, String> validatorResult = authService.validateHandling(errors);
            for (String key : validatorResult.keySet()) {
                model.addAttribute(key, validatorResult.get(key));
            }
            return "registerFirst";
        }

        // 회원가입 성공 후, 로그인 화면으로 redirect
        return "redirect:/registerSecond";
    }

    // 이메일 인증 코드 전송
    @PostMapping("/register/send-verification")
    public String sendVerificationEmail(@RequestParam String email, Model model, RegisterDto registerDto) {
        model.addAttribute("registerDto", registerDto);

        try {
            authService.sendVerificationCode(email);
            model.addAttribute("message", "인증 코드가 이메일로 전송되었습니다.");
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "registerSecond";
    }

    // 이메일 인증 코드 검증
    @PostMapping("/register/verify-code")
    public String verifyEmailCode(@RequestParam String email, @RequestParam String verificationCode, Model model, RegisterDto registerDto) {
        model.addAttribute("registerDto", registerDto);

        try {
            authService.verifyCode(email, verificationCode);
            model.addAttribute("emailVerified", true);
            return "redirect:/registerSecond";
        } catch (RuntimeException e) {
            model.addAttribute("error", "인증 코드가 잘못되었습니다: " + e.getMessage());
        }
        return "registerSecond";
    }

    // 회원가입 이메일 제외 나머지 입력 정보 전송
    @PostMapping("/registerSecond")
    public String doRegisterSecond(@Valid RegisterDto registerDto, Errors errors, Model model) {

        if (errors.hasErrors()) {
            model.addAttribute("registerDto", registerDto);
            // 유효성 검사를 통과 못한 필드와 메시지 핸들링
            Map<String, String> validatorResult = authService.validateHandling(errors);
            for (String key : validatorResult.keySet()) {
                model.addAttribute(key, validatorResult.get(key));
            }
            return "registerSecond";
        }

        // 회원가입 로직
        authService.register(registerDto);

        // 회원가입 성공 후, 로그인 화면으로 redirect
        return "redirect:/login";
    }
}