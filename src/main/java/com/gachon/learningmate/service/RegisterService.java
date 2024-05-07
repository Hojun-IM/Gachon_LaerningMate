package com.gachon.learningmate.service;

import com.gachon.learningmate.data.dto.RegisterDto;
import com.gachon.learningmate.data.entity.User;
import com.gachon.learningmate.data.repository.UserRepository;
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
    public void register(RegisterDto registerDto) {
        try {
            // 아이디 중복 검사
            validateUserId(registerDto.getUserId());

            // 패스워드 암호화 및 회원 정보 생성
            String encryptedPassword = passwordEncoder.encode(registerDto.getPassword());
            User newUser = User.builder()
                    .userId(registerDto.getUserId())
                    .password(encryptedPassword)
                    .username(registerDto.getUsername())
                    .email(registerDto.getEmail())
                    .birth(parseDate(registerDto.getBirth()))
                    .type(registerDto.getType())
                    .build();

            userRepository.save(newUser);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.", e);
        }
    }

    // 회원가입 DTO에 설정된 유효성 검사
    @Transactional(readOnly = true)
    public Map<String, String> validateHandling(Errors errors) {
        Map<String, String> validatorResult = new HashMap<>();
        // 유효성 검사 실패한 필드 목록 받아오기
        for (FieldError error : errors.getFieldErrors()) {
            String errorKey = String.format("error_%s", error.getField());
            validatorResult.put(errorKey, error.getDefaultMessage());
        }
        return validatorResult;
    }

    // 날짜 변환
    private Date parseDate(String birth) {
        // 문자열로 입력된 날짜를 Date 객체로 변환
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        try {
            return dateFormat.parse(birth);
        } catch (ParseException e) {
            throw new RuntimeException("생년월일 형식이 올바르지 않습니다.");
        }
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

    // 인증 코드 전송
    @Transactional
    public void sendVerificationCode(String email) {
        // 이메일 유효성 검사
        validateEmail(email);

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

    // 이메일 유효성 확인
    public void validateEmail(String email) {
        // 양식 확인
        if (!email.matches("[\\w.-]+@gachon\\.ac\\.kr")){
            throw new RuntimeException("가천대학교 이메일을 사용해주세요.");
        }

        // 중복 확인
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("중복된 이메일 주소입니다.");
        }
    }

    // 아이디 유효성 확인
    private void validateUserId(String userId) {
        // 아이디 중복 확인
        if (userRepository.existsByUserId(userId)) {
            throw new RuntimeException("이미 사용 중인 아이디입니다.");
        }
    }

}