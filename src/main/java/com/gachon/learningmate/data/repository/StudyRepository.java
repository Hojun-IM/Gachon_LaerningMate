package com.gachon.learningmate.data.repository;

import com.gachon.learningmate.data.dto.StudyDto;
import com.gachon.learningmate.data.entity.Study;
import com.gachon.learningmate.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyRepository extends JpaRepository<Study, Long> {

    // User를 통해 Study 검색
    Study findByCreatorId(User creator);

    // 스터디 아이디로 스터디 검색
    Study findByStudyId(int studyId);
}
