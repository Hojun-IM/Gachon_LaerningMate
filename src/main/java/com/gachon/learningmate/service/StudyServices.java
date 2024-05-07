package com.gachon.learningmate.service;

import com.gachon.learningmate.data.dto.StudyDto;
import com.gachon.learningmate.data.entity.Study;
import com.gachon.learningmate.data.repository.StudyRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudyServices {

    private final StudyRepository studyRepository;

    @Autowired
    public StudyServices(StudyRepository studyRepository) {
        this.studyRepository = studyRepository;
    }

    // 스터디 조회
    public List<Study> findAllStudy() {
        return studyRepository.findAll();
    }

    // 스터디 생성
    @Transactional
    public void createStudy(StudyDto studyDto) {

        Study study = Study.builder()
                .creatorId(studyDto.getCreatorId())
                .studyName(studyDto.getStudyName())
                .description(studyDto.getDescription())
                .status(studyDto.getStatus())
                .category(studyDto.getCategory())
                .location(studyDto.getLocation())
                .maxMember(studyDto.getMaxMember())
                .currentMember(studyDto.getCurrentMember())
                .build();

        studyRepository.save(study);
    }

}
