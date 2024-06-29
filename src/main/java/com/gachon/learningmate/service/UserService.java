package com.gachon.learningmate.service;

import com.gachon.learningmate.data.entity.User;
import com.gachon.learningmate.data.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findUserById(String userId) {
        return userRepository.findByUserId(userId);
    }
}
