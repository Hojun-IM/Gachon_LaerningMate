package com.gachon.learningmate.data.repository;

import com.gachon.learningmate.data.entity.Study;
import com.gachon.learningmate.data.entity.StudyJoin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyJoinRepository extends JpaRepository<StudyJoin, Long> {

    // 스터디 아이디로 스터디 신청 검색
    List<StudyJoin> findByStudy(Study study);
}
