package com.gachon.learningmate.data.repository;

import com.gachon.learningmate.data.entity.Favorite;
import com.gachon.learningmate.data.entity.Study;
import com.gachon.learningmate.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    // 사용자랑 스터디 조회
    boolean existsByUserAndStudy(User user, Study study);

    // 즐겨찾기 조회
    Favorite findByUserAndStudy(User user, Study study);
    
    // 즐겨찾기 조회
    List<Favorite> findByUser(User user);
}
