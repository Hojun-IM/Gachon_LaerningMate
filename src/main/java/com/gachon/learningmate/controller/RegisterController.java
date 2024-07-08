package com.gachon.learningmate.controller;

import com.gachon.learningmate.data.dto.RegisterDto;
import com.gachon.learningmate.service.RegisterService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

@Controller
@RequestMapping("/register")
@SessionAttributes("registerDto")
public class RegisterController {

    private final RegisterService registerService;

    @Autowired
    public RegisterController(RegisterService registerService) {
        this.registerService = registerService;
    }

    // 초기화된 회원가입 데이터 객체 생성
    @ModelAttribute("registerDto")
    public RegisterDto registerDto() {
        return new RegisterDto();
    }

    // 회원가입 페이지 출력
    @GetMapping
    public String showRegisterForm() {
        return "register";
    }

    // 회원가입 정보 처리 및 검증
    @PostMapping
    public String processRegisterForm(@Valid @ModelAttribute RegisterDto registerDto,
                                      Errors errors, Model model,
                                      SessionStatus status, HttpSession session) {
        // 이메일 인증 여부 확인
        if (!registerService.isEmailVerified(session)) {
            model.addAttribute("error_verification", "이메일 인증이 완료되지 않았습니다.");
            return "register";
        }

        // 유효성 검사 오류 확인
        if (errors.hasErrors()) {
            model.addAllAttributes(registerService.validateHandling(errors));
            return "register";
        }

        // 회원가입 로직 수행
        try {
            registerService.register(registerDto);
            status.setComplete();
            session.removeAttribute("emailVerified");
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error_userId", e.getMessage());
            return "register";
        }
    }

    // 회원가입 인증 코드 전송
    @PostMapping("/send-verification")
    public String sendVerificationEmail(@RequestParam String email,
                                        @ModelAttribute RegisterDto registerDto,
                                        Model model) {
        try {
            registerService.sendVerificationCode(email);
            // 세션에 이메일 정보 저장
            registerDto.setEmail(email);
            model.addAttribute("message_email", "인증 코드가 이메일로 전송되었습니다.");
        } catch (Exception e) {
            model.addAttribute("error_email", e.getMessage());
        }
        return "register";
    }

    // 회원가입 인증 코드 확인
    @PostMapping("/verify-code")
    public String checkVerificationCode(@RequestParam String verificationCode,
                                        @ModelAttribute RegisterDto registerDto,
                                        Model model, HttpSession session) {
        try {
            registerService.verifyCode(registerDto.getEmail(), verificationCode);
            registerService.completeEmailVerification(session);
            model.addAttribute("message_verification", "이메일 인증이 완료되었습니다.");
        } catch (Exception e) {
            model.addAttribute("error_verificationCode", e.getMessage());
            registerService.failEmailVerification(session);
        }
        return "register";
    }
}