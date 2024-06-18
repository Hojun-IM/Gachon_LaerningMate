package com.gachon.learningmate.service;

import com.gachon.learningmate.data.dto.RegisterDto;
import com.gachon.learningmate.data.entity.User;
import com.gachon.learningmate.data.repository.UserRepository;
import com.gachon.learningmate.exception.DuplicateEmailException;
import com.gachon.learningmate.exception.DuplicateUserIdException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;

@Service
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

    // 회원가입
    @Transactional
    public void register(RegisterDto registerDto) {
        validateUserId(registerDto.getUserId());

        // 패스워드 암호화
        String encryptedPassword = passwordEncoder.encode(registerDto.getPassword());
        registerDto.setPassword(encryptedPassword);

        // 회원 정보 생성 및 저장
        User newUser = registerDto.toEntity();
        userRepository.save(newUser);
    }

    // 인증 코드 전송
    @Transactional
    public void sendVerificationCode(String email) {
        // 이메일 유효성 검사
        validateEmail(email);
        validateEmailFormat(email);

        // 인증 코드 생성
        String verificationCode = createVerificationCode();

        // Redis에 인증 번호 저장
        Duration expiration = Duration.ofMinutes(5);
        redisService.setValues(email, verificationCode, expiration);

        // 메일 전송
        mailService.sendMail(email, "Gachon LearningMate 이메일 인증 코드", "인증 코드: " + verificationCode);
    }

    // 인증 코드 확인
    @Transactional
    public void verifyCode(String email, String verificationCode){
        String storedCode  = redisService.getValues(email);

        if(!verificationCode.equals(storedCode)){
            throw new RuntimeException("인증 코드가 올바르지 않습니다.");
        }

        redisService.deleteValues(email);
    }

    // 이메일 인증 코드 생성
    public String createVerificationCode() {
        int length = 6;
        try {
            Random random = SecureRandom.getInstanceStrong();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < length; i++) {
                builder.append(random.nextInt(10));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("인증코드 생성에 실패했습니다.");
        }
    }

    // DTO에 설정된 유효성 검사
    public Map<String, String> validateHandling(Errors errors) {
        Map<String, String> validatorResult = new HashMap<>();
        for (FieldError error : errors.getFieldErrors()) {
            String errorKey = String.format("error_%s", error.getField());
            validatorResult.put(errorKey, error.getDefaultMessage());
        }
        return validatorResult;
    }

    // 이메일 유효성 확인
    public void validateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException("중복된 이메일 주소입니다.");
        }
    }

    // 이메일 유효성 확인
    public void validateEmailFormat(String email) {
        if (!email.matches("[\\w.-]+@gachon\\.ac\\.kr")) {
            throw new IllegalArgumentException("가천대학교 이메일을 사용해주세요.");
        }
    }

    // 아이디 유효성 확인
    private void validateUserId(String userId) {
        if (userRepository.existsByUserId(userId)) {
            throw new DuplicateUserIdException("이미 사용 중인 아이디입니다.");
        }
    }

    // 이메일 인증 여부 확인
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