package com.gachon.learningmate.service;

import com.gachon.learningmate.data.dto.RegisterDto;
import com.gachon.learningmate.data.entity.User;
import com.gachon.learningmate.data.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final UserRepository userRepository;

    @Autowired
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
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

    // 회원가입
    public void register(RegisterDto registerDto) {
        //비밀번호 암호화

        // birth 문자열을 Date로 변환
        Date birthDate = null;
        if (registerDto.getBirth() != null && !registerDto.getBirth().isEmpty()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            try {
                birthDate = dateFormat.parse(registerDto.getBirth());
            } catch (ParseException e) {
                // 여기서는 예외를 적절히 처리하거나, 변환 실패 시 사용자에게 알릴 수 있는 메커니즘을 구현해야 합니다.
                // 예외 처리 또는 사용자에게 전달할 메시지 처리
                throw new RuntimeException("생년월일 형식이 올바르지 않습니다.");
            }
        }

        // registerDto의 사용자 정보를 엔티티 객체로 변환
        User user = User.builder()
                .userId(registerDto.getUserId())
                .password(registerDto.getPassword())
                .username(registerDto.getUsername())
                .email(registerDto.getEmail())
                .birth(birthDate)
                .type(registerDto.getType())
                .build();

        // 사용자 정보 저장
        userRepository.save(user);
    }

    // 로그인 로직
}