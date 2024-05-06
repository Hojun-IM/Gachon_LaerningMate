package com.gachon.learningmate.service;

import com.gachon.learningmate.data.dto.UserPrincipalDetails;
import com.gachon.learningmate.data.entity.User;
import com.gachon.learningmate.data.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        // 입력 받은 ID로 회원 검색
        User user = userRepository.findByUserId(userId);
        System.out.println("userId = " + userId);
        System.out.println("user = " + user);

        if (user == null) {
            throw new UsernameNotFoundException(userId + "를 찾을 수 없습니다.");
        }

        return new UserPrincipalDetails(user);
    }
}
