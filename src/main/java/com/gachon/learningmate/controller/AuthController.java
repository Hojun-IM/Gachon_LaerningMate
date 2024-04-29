package com.gachon.learningmate.controller;

import com.gachon.learningmate.data.dto.LoginDto;
import com.gachon.learningmate.data.dto.RegisterDto;
import com.gachon.learningmate.service.AuthService;
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
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // 모든 요청에 대해 모델에 registerDto가 없으면 새로 추가
    @ModelAttribute("registerDto")
    public RegisterDto registerDto() {
        // registerDto가 전달될 때 값이 없어 발생하는 문제를 해결하기 위해 초기화
        RegisterDto registerDto = new RegisterDto();
        registerDto.setUserId("");
        registerDto.setPassword("");
        registerDto.setUsername("");
        registerDto.setEmail("");
        registerDto.setBirth("");
        return registerDto;
    }

    // 로그인 화면 출력
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    // 회원가입 화면 출력 (이메일 인증 단계)
    @GetMapping("/registerFirst")
    public String showRegisterFromFirst(Model model) {
        return "registerFirst";
    }

    // 회원가입 화면 출력 (나머지 정보 입력 단계)
    @GetMapping("/registerSecond")
    public String showRegisterFromSecond() {
        return "registerSecond";
    }

    // 이메일 인증 코드 전송
    @PostMapping("/register/send-verification")
    public String sendVerificationEmail(@RequestParam String email, Model model, @ModelAttribute("registerDto") RegisterDto registerDto) {

        try {
            authService.sendVerificationCode(email);
            model.addAttribute("message_email", "인증 코드가 이메일로 전송되었습니다.");
            registerDto.setEmail(email);
            model.addAttribute("registerDto", registerDto);
        } catch (RuntimeException e) {
            model.addAttribute("error_email", e.getMessage());
        }
        return "registerFirst";
    }

    // 이메일 인증 코드 검증
    @PostMapping("/register/verify-code")
    public String verifyEmailCode(@RequestParam String verificationCode, Model model, @ModelAttribute("registerDto") RegisterDto registerDto) {

        try {
            authService.verifyCode(registerDto.getEmail(), verificationCode);
            model.addAttribute("registerDto", registerDto);
            return "redirect:/registerSecond";
        } catch (RuntimeException e) {
            model.addAttribute("error_verificationCode", e.getMessage());
            return "registerFirst";
        }
    }

    // 회원가입 이메일 제외 나머지 입력 정보 전송
    @PostMapping("/registerSecond")
    public String doRegisterSecond(@ModelAttribute("registerDto") @Valid RegisterDto registerDto, Errors errors, Model model, SessionStatus status) {
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
        
        // session에서 registerDto 제거
        status.setComplete();

        // 회원가입 성공 후, 로그인 화면으로 redirect
        return "redirect:/login";
    }

    // 로그인 전송

}