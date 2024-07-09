package com.gachon.learningmate.service;

import com.gachon.learningmate.data.dto.RegisterDto;
import com.gachon.learningmate.data.entity.User;
import com.gachon.learningmate.data.repository.UserRepository;
import com.gachon.learningmate.exception.DuplicateEmailException;
import com.gachon.learningmate.exception.DuplicateUserIdException;
import com.gachon.learningmate.exception.InvalidEmailFormatException;
import com.gachon.learningmate.exception.InvalidVerificationCodeException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class RegisterService {

    private final UserRepository userRepository;
    private final MailService mailService;
    private final RedisService redisService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public RegisterService(UserRepository userRepository, MailService mailService, RedisService redisService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.mailService = mailService;
        this.redisService = redisService;
        this.passwordEncoder = passwordEncoder;
    }

    // 회원가입 실행
    public void register(RegisterDto registerDto) {
        validateRegistration(registerDto);
        User newUser = createUser(registerDto);
        userRepository.save(newUser);
    }

    // 인증 코드 전송
    public void sendVerificationCode(String email) {
        validateEmail(email);
        String verificationCode = generateVerificationCode();
        storeVerificationCode(email, verificationCode);
        sendVerificationEmail(email, verificationCode);
    }

    // 인증 코드 확인
    public void verifyCode(String email, String verificationCode) {
        String storedCode = redisService.getValues(email);
        if (storedCode == null || !verificationCode.equals(storedCode)) {
            throw new InvalidVerificationCodeException("인증 코드가 올바르지 않습니다.");
        }
        redisService.deleteValues(email);
    }

    // DTO에 설정된 유효성 검사
    public Map<String, String> validateHandling(Errors errors) {
        return errors.getFieldErrors().stream()
                .collect(Collectors.toMap(
                        error -> String.format("error_%s", error.getField()),
                        FieldError::getDefaultMessage
                ));
    }

    // 사용자 ID와 EMAIL 검사
    private void validateRegistration(RegisterDto registerDto) {
        validateUserId(registerDto.getUserId());
        validateEmailFormat(registerDto.getEmail());
    }

    // 사용자 생성
    private User createUser(RegisterDto registerDto) {
        registerDto.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        return registerDto.toEntity();
    }

    // EMAIL 유효성 검사
    private void validateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException("중복된 이메일 주소입니다.");
        }
        validateEmailFormat(email);
    }

    // 이메일 형식 검사
    private void validateEmailFormat(String email) {
        if (!email.matches("[\\w.-]+@gachon\\.ac\\.kr")) {
            throw new InvalidEmailFormatException("가천대학교 이메일을 사용해주세요.");
        }
    }

    // 사용자 ID 유효성 검사
    private void validateUserId(String userId) {
        if (userRepository.existsByUserId(userId)) {
            throw new DuplicateUserIdException("이미 사용 중인 아이디입니다.");
        }
    }

    // 인증 코드 생성
    private String generateVerificationCode() {
        return new Random().ints(6, 0, 10)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining());
    }

    // 인증 코드 Redis에 저장
    private void storeVerificationCode(String email, String verificationCode) {
        redisService.setValues(email, verificationCode, Duration.ofMinutes(5));
    }

    // 인증 EMAIL 전송
    private void sendVerificationEmail(String email, String verificationCode) {
        mailService.sendMail(email, "Gachon LearningMate 이메일 인증 코드", "인증 코드: " + verificationCode);
    }

    // 이메일 인증 여부 검사
    public boolean isEmailVerified(HttpSession session) {
        Boolean emailVerified = (Boolean) session.getAttribute("emailVerified");
        return emailVerified != null && emailVerified;
    }

    // 이메일 인증 완료 처리
    public void completeEmailVerification(HttpSession session) {
        session.setAttribute("emailVerified", true);
    }

    // 이메일 인증 실패 처리
    public void failEmailVerification(HttpSession session) {
        session.setAttribute("emailVerified", false);
    }
}