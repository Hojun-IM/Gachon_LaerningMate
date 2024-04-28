package com.gachon.learningmate.service;

import com.gachon.learningmate.data.dto.LoginDto;
import com.gachon.learningmate.data.dto.RegisterDto;
import com.gachon.learningmate.data.entity.User;
import com.gachon.learningmate.data.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
public class AuthService {

    private final UserRepository userRepository;
    private final MailService mailService;
    private final RedisService redisService;

    @Autowired
    public AuthService(UserRepository userRepository, MailService mailService, RedisService redisService) {
        this.userRepository = userRepository;
        this.mailService = mailService;
        this.redisService = redisService;
    }

    // 이메일 유효성 확인
    public void checkEmail(String email) {
        // 이메일 양식 확인
        if (!email.matches("[\\w.-]+@gachon\\.ac\\.kr")){
            throw new RuntimeException("가천대학교 이메일을 사용해주세요.");
        }

        // 중복 확인
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("중복된 이메일 주소입니다.");
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
        // 전송 전 이메일 중복 확인
        try {
            this.checkEmail(email);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
        
        // 인증 코드 생성
        String verificationCode = createVerificationCode();

        // Redis에 인증번호 저장
        Duration duration = Duration.ofMinutes(5);
        redisService.setValues(email, verificationCode, duration);
        
        // 메일 전송
        mailService.sendMail(email, "Gachon LearningMate 이메일 인증 코드", "인증 코드: " + verificationCode);
    }

    // 인증 코드 확인
    @Transactional
    public void verifyCode(String email, String authCode){
        String savedCode = redisService.getValues(email);
        if(!authCode.equals(savedCode)){
            throw new RuntimeException("인증 코드가 올바르지 않습니다.");
        }
        redisService.deleteValues(email);
    }

    // 회원가입 유효성 검사
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
    private Date parseDate(RegisterDto registerDto) {
        // birth 문자열을 Date로 변환
        Date birthDate = null;
        if (registerDto.getBirth() != null && !registerDto.getBirth().isEmpty()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            try {
                birthDate = dateFormat.parse(registerDto.getBirth());
            } catch (ParseException e) {
                throw new RuntimeException("생년월일 형식이 올바르지 않습니다.");
            }
        }
        return birthDate;
    }

    // 회원가입을 위한 User 객체 생성
    private User createUserFromDto(RegisterDto registerDto) {
        Date birthDate = parseDate(registerDto);
        return User.builder()
                .userId(registerDto.getUserId())
                .password(registerDto.getPassword())
                .username(registerDto.getUsername())
                .email(registerDto.getEmail())
                .birth(birthDate)
                .type(registerDto.getType())
                .build();
    }

    // 회원가입
    public void register(RegisterDto registerDto) {
        //비밀번호 암호화

        // registerDto의 사용자 정보를 엔티티 객체로 변환
        User user = createUserFromDto(registerDto);
        // 사용자 정보 저장
        userRepository.save(user);
    }

    // 로그인 로직

}