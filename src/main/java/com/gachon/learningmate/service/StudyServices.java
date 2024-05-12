package com.gachon.learningmate.service;

import com.gachon.learningmate.data.dto.StudyDto;
import com.gachon.learningmate.data.entity.Study;
import com.gachon.learningmate.data.entity.User;
import com.gachon.learningmate.data.repository.StudyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                .photoPath(studyDto.getPhotoPath())
                .build();

        studyRepository.save(study);
    }

    // 스터디 업데이트
    @Transactional
    public Study updateStudy(StudyDto studyDto, User currentUser) {
        Study study = studyRepository.findByCreatorId(currentUser);

        // 스터디 존재 여부 확인
        if (study == null) {
            throw new IllegalArgumentException("해당 스터디를 찾을 수 없습니다.");
        }

        // 사용자 존재 여부 확인
        if (currentUser == null) {
            throw new IllegalArgumentException("회원을 찾을 수 없습니다.");
        }

        // 변경하고자 하는 스터디 생성자인지 확인
        if (!studyDto.getCreatorId().equals(study.getCreatorId())) {
            throw new IllegalStateException("스터디를 업데이트할 권한이 없습니다.");
        }

        // 변경 정보 업데이트
        study = Study.builder()
                .creatorId(studyDto.getCreatorId())
                .studyName(studyDto.getStudyName())
                .description(studyDto.getDescription())
                .status(studyDto.getStatus())
                .category(studyDto.getCategory())
                .location(studyDto.getLocation())
                .maxMember(studyDto.getMaxMember())
                .currentMember(studyDto.getCurrentMember())
                .build();

        return studyRepository.save(study);
    }

    // 스터디 삭제
    public void deleteStudy(StudyDto studyDto, User currentUser) {
        Study study = studyRepository.findByCreatorId(currentUser);

        // 스터디 존재 여부 확인
        if (study == null) {
            throw new IllegalArgumentException("해당 스터디를 찾을 수 없습니다.");
        }

        // 사용자 존재 여부 확인
        if (currentUser == null) {
            throw new IllegalArgumentException("회원을 찾을 수 없습니다.");
        }

        // 변경하고자 하는 스터디 생성자인지 확인
        if (!studyDto.getCreatorId().equals(study.getCreatorId())) {
            throw new IllegalStateException("스터디를 업데이트할 권한이 없습니다.");
        }

        studyRepository.delete(study);
    }

    // 스터디 DTO에 설정된 유효성 검사
    @Transactional(readOnly = true)
    public Map<String, String> validateHandling(BindingResult result) {
        Map<String, String> validatorResult = new HashMap<>();
        // 유효성 검사 실패한 필드 목록 받아오기
        for (FieldError error : result.getFieldErrors()) {
            String errorKey = String.format("error_%s", error.getField());
            validatorResult.put(errorKey, error.getDefaultMessage());
        }
        return validatorResult;
    }
}
