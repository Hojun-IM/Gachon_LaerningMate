package com.gachon.learningmate.data.repository;

import com.gachon.learningmate.data.dto.StudyJoinDto;
import com.gachon.learningmate.data.entity.StudyJoin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyJoinRepository extends JpaRepository<StudyJoin, Long> {

    public void save(StudyJoinDto studyJoinDto);
}
