package com.gachon.learningmate.data.repository;

import com.gachon.learningmate.data.entity.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyRepository extends JpaRepository<Study, Long> {

    // 모든 스터디 목록 반환
    List<Study> findAllStudy();

    // ID로 스터디 검색
    Study findById(int id);
}
