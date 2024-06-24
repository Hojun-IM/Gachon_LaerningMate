package com.gachon.learningmate.data.repository;

import com.gachon.learningmate.data.entity.Study;
import com.gachon.learningmate.data.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyRepository extends JpaRepository<Study, Long> {

    // 스터디 아이디로 스터디 검색
    Study findByStudyId(int studyId);

    // 스터디 아이디로 스터디 목록 검색
    Page<Study> findStudyByCreatorId(User user, Pageable pageable);

    // 최근 생성 스터디 조회
    Page<Study> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
