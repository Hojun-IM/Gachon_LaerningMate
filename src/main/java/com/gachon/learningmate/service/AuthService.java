package com.gachon.learningmate.service;

import com.gachon.learningmate.data.dto.RegisterDto;
import com.gachon.learningmate.data.entity.User;
import com.gachon.learningmate.data.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class AuthService {

    private final UserRepository userRepository;

    @Autowired
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 회원가입
    public void register(RegisterDto registerDto) {
        //비밀번호 암호화

        // Birth 문자열을 Date로 변환
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date birthDate = null;
        try {
            birthDate = dateFormat.parse(registerDto.getBirth());
        } catch (ParseException e) {
            throw new RuntimeException(e);
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
