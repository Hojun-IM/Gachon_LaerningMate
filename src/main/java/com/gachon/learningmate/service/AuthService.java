package com.gachon.learningmate.service;

import com.gachon.learningmate.data.dto.RegisterDto;
import com.gachon.learningmate.data.entity.User;
import com.gachon.learningmate.data.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

        // registerDto의 사용자 정보를 엔티티 객체로 변환
        User user = User.builder()
                .userId(registerDto.getUserId())
                .password(registerDto.getPassword())
                .username(registerDto.getEmail())
                .email(registerDto.getEmail())
                .build();
        
        // 사용자 정보 저장
        userRepository.save(user);
    }
    
    // 로그인 로직
}
