package com.gachon.learningmate.service;

import com.gachon.learningmate.config.FileUploadUtil;
import com.gachon.learningmate.data.dto.StudyDto;
import com.gachon.learningmate.data.dto.UserPrincipalDetails;
import com.gachon.learningmate.data.entity.Study;
import com.gachon.learningmate.data.repository.StudyRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class StudyServices {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private final StudyRepository studyRepository;

    @Autowired
    public StudyServices(StudyRepository studyRepository) {
        this.studyRepository = studyRepository;
    }

    // 스터디 조회
    public Page<Study> findAllStudy(Pageable pageable) {
        return studyRepository.findAll(pageable);
    }

    // 스터디 생성
    @Transactional
    public void createStudy(StudyDto studyDto) {
        Study study = buildStudy(studyDto);
        studyRepository.save(study);
    }

    // 스터디 업데이트
    @Transactional
    public Study updateStudy(StudyDto studyDto, Study study, UserPrincipalDetails currentUser) {
        try {
            validateStudyAndUser(study, currentUser);
            // 변경 정보 업데이트
            study = buildStudy(studyDto);
            return studyRepository.save(study);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // 스터디 삭제
    public void deleteStudy(Study study, UserPrincipalDetails currentUser) {
        try {
            validateStudyAndUser(study, currentUser);
            studyRepository.delete(study);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
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

    @Transactional
    public boolean validatePhoto(MultipartFile photo, StudyDto studyDto) throws IOException {

        // 사진 파일 유효성 검사
        if (!FileUploadUtil.isExtensionValid(photo)){
            throw new IOException("허용되는 파일 형식은 jpg, jpeg, png입니다.");
        }

        // 기본 사진 설정
        if (photo == null || photo.isEmpty()) {
            studyDto.setPhotoPath("/img/default-study.jpg");
            return true;
        }

        // 파일 처리
        String fileName = StringUtils.cleanPath(photo.getOriginalFilename());
        FileUploadUtil.saveFile(uploadDir, fileName, photo);
        studyDto.setPhotoPath("/img/study-logo/" + fileName);
        return true;
    }

    // 스터디 엔티티 빌드
    private Study buildStudy(StudyDto studyDto) {
        return Study.builder()
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
    }

    // 스터디와 사용자 유효성 검사
    private boolean validateStudyAndUser(Study study, UserPrincipalDetails currentUser) {
        if (!studyRepository.existsByStudyId(study.getStudyId())) {
            throw new IllegalArgumentException("해당 스터디를 찾을 수 없습니다.");
        }

        if (currentUser == null) {
            throw new IllegalArgumentException("회원을 찾을 수 없습니다.");
        }

        if (!study.getCreatorId().equals(study.getCreatorId())) {
            throw new IllegalStateException("스터디를 업데이트할 권한이 없습니다.");
        }
        return true;
    }

}
