package com.gachon.learningmate.data.repository;

import com.gachon.learningmate.data.entity.Study;
import com.gachon.learningmate.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyRepository extends JpaRepository<Study, Long> {

    // User를 통해 Study 검색
    Study findByCreatorId(User creator);
}
