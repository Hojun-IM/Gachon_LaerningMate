package com.gachon.learningmate.service;

import com.gachon.learningmate.data.dto.UserPrincipalDetails;
import com.gachon.learningmate.data.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 로그인 된 사용자 정보 가져오기
    public UserPrincipalDetails getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new RuntimeException("인증 정보를 가져올 수 없습니다.");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserPrincipalDetails) {
            return (UserPrincipalDetails) principal;
        } else {
            throw new RuntimeException("인증 정보가 올바르지 않습니다.");
        }
    }
}
