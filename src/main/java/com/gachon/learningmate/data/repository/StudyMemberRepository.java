package com.gachon.learningmate.data.repository;

import com.gachon.learningmate.data.entity.Study;
import com.gachon.learningmate.data.entity.StudyMember;
import com.gachon.learningmate.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudyMemberRepository extends JpaRepository<StudyMember, Long> {

    // 스터디에 가입된 멤버 리스트
    List<StudyMember> findByStudy(Study study);

    // 스터디 존재 여부 확인
    Optional<StudyMember> findByStudyAndUser(Study study, User user);
}
