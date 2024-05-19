package com.gachon.learningmate.data.repository;

import com.gachon.learningmate.data.entity.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyRepository extends JpaRepository<Study, Long> {

    // 스터디 아이도로 스터디 존재 여부 확인
    boolean existsByStudyId(int studyId);

    // 스터디 아이디로 스터디 검색
    Study findByStudyId(int studyId);
}
