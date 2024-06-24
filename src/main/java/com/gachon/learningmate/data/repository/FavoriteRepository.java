package com.gachon.learningmate.data.repository;

import com.gachon.learningmate.data.entity.Favorite;
import com.gachon.learningmate.data.entity.Study;
import com.gachon.learningmate.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    boolean existsByUserAndStudy(User user, Study study);

    void deleteByUserAndStudy(User user, Study study);
}
