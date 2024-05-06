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

import java.util.Map;

@Controller
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
        return RegisterDto.builder()
                .userId("")
                .password("")
                .username("")
                .email("")
                .birth("")
                .verificationCode("")
                .build();
    }

    // 회원가입 페이지 출력
    @GetMapping("/register")
    public String showRegisterForm() {
        return "register";
    }

    // 회원가입 정보 처리 및 검증
    @PostMapping("/register")
    public String processRegisterForm(@ModelAttribute("registerDto") @Valid RegisterDto registerDto, Errors errors, Model model, SessionStatus status, HttpSession session) {
        // 세션에서 이메일 인증 여부 확인
        Boolean emailVerified = (Boolean) session.getAttribute("emailVerified");

        // 이메일 인증 미완료 시 오류 메시지를 추가
        if (Boolean.FALSE.equals(emailVerified)) {
            model.addAttribute("error_verification", "이메일 인증이 완료되지 않았습니다.");
            model.addAttribute("registerDto", registerDto);
            return "register";
        }

        // 다른 필드에 대한 유효성 검사 오류 확인
        if (errors.hasErrors() || Boolean.FALSE.equals(emailVerified)) {
            model.addAttribute("registerDto", registerDto);

            // 유효성 검사를 통과하지 못한 필드의 오류 메시지를 추가
            Map<String, String> validatorResult = registerService.validateHandling(errors);
            for (String key : validatorResult.keySet()) {
                model.addAttribute(key, validatorResult.get(key));
            }
            return "register";
        }

        // 모든 검증을 통과한 경우 회원가입 로직 수행
        registerService.register(registerDto);
        status.setComplete();
        return "redirect:/login";
    }

    // 회원가입 인증 코드 전송
    @PostMapping("/register/send-verification")
    public String sendVerificationEmail(@RequestParam String email, Model model, @ModelAttribute("registerDto") RegisterDto registerDto) {
        try {
            registerService.sendVerificationCode(email);
            // 세션 registerDto에 이메일 정보 저장
            registerDto.setEmail(email);
            model.addAttribute("registerDto", registerDto);
            model.addAttribute("message_email", "인증 코드가 이메일로 전송되었습니다.");
        } catch (RuntimeException e) {
            model.addAttribute("error_email", e.getMessage());
        }
        return "register";
    }

    // 회원가입 인증 코드 확인
    @PostMapping("/register/verify-code")
    public String checkVerificationCode(@RequestParam String verificationCode, Model model, @ModelAttribute("registerDto") RegisterDto registerDto, HttpSession session) {
        try {
            registerService.verifyCode(registerDto.getEmail(), verificationCode);
            // 인증 성공 시 세션에 이메일 인증 상태를 저장
            session.setAttribute("emailVerified", true);
            model.addAttribute("message_verification", "이메일 인증이 완료되었습니다.");
            return "register";
        } catch (RuntimeException e) {
            model.addAttribute("error_verificationCode", e.getMessage());
            // 인증 실패 시 세션에 인증 실패 상태를 저장
            session.setAttribute("emailVerified", false);
            return "register";
        }
    }

}