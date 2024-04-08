package com.gachon.learningmate.data.repository;

import com.gachon.learningmate.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    // userId를 통해 User엔티티 조회
    User findByUserId(String userId);

    // email 인증을 위해 이메일 존재 여부 검색
    boolean existsByEmail(String email);
}
