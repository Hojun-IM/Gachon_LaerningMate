package com.gachon.learningmate.service;

import com.gachon.learningmate.config.FileUploadUtil;
import com.gachon.learningmate.config.PageItem;
import com.gachon.learningmate.data.dto.StudyDto;
import com.gachon.learningmate.data.dto.StudyJoinDto;
import com.gachon.learningmate.data.dto.UserPrincipalDetails;
import com.gachon.learningmate.data.entity.Study;
import com.gachon.learningmate.data.entity.StudyJoin;
import com.gachon.learningmate.data.entity.StudyJoinRole;
import com.gachon.learningmate.data.entity.StudyMember;
import com.gachon.learningmate.data.repository.StudyJoinRepository;
import com.gachon.learningmate.data.repository.StudyMemberRepository;
import com.gachon.learningmate.data.repository.StudyRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StudyServices {

    // 사진 업로드 경로
    @Value("${file.upload-dir}")
    private String uploadDir;

    private final StudyRepository studyRepository;
    private final StudyJoinRepository studyJoinRepository;
    private final StudyMemberRepository studyMemberRepository;

    @Autowired
    public StudyServices(StudyRepository studyRepository, StudyJoinRepository studyJoinRepository, StudyMemberRepository studyMemberRepository) {
        this.studyRepository = studyRepository;
        this.studyJoinRepository = studyJoinRepository;
        this.studyMemberRepository = studyMemberRepository;
    }

    // 스터디 조회
    public Page<Study> findAllStudy(Pageable pageable) {
        return studyRepository.findAll(pageable);
    }

    // 스터디 생성
    @Transactional
    public void createStudy(StudyDto studyDto, MultipartFile photo, BindingResult result) throws IOException {
        UserPrincipalDetails userPrincipalDetails = getAuthentication();
        studyDto.setCreatorId(userPrincipalDetails.getUser());

        validatePhoto(photo, studyDto);

        if (result.hasErrors()) {
            throw new IllegalArgumentException("스터디 DTO 필드 유효성 검사 오류");
        }

        // 기본 사진 경로 설정
        if (studyDto.getPhotoPath() == null || studyDto.getPhotoPath().isEmpty()) {
            studyDto.setPhotoPath("/img/default-study.jpg");
        }

        Study study = studyDto.toEntity();
        studyRepository.save(study);

        // 스터디 생성자를 StudyMember로 추가
        StudyMember leader = new StudyMember();
        leader.setStudy(study);
        leader.setUser(userPrincipalDetails.getUser());
        leader.setJoinDate(new Date());
        leader.setRole(StudyJoinRole.LEADER);

        studyMemberRepository.save(leader);
    }

    // 스터디 업데이트
    @Transactional
    public void updateStudy(StudyDto studyDto, MultipartFile photo, BindingResult result) throws IOException {
        Study existingStudy = studyRepository.findByStudyId(studyDto.getStudyId());
        UserPrincipalDetails principalDetails = getAuthentication();

        // Validate the user and study
        validateStudyAndUser(existingStudy, principalDetails);

        if (photo != null && !photo.isEmpty()) {
            validatePhoto(photo, studyDto);
        } else {
            studyDto.setPhotoPath(existingStudy.getPhotoPath());
        }

        if (result.hasErrors()) {
            throw new IllegalArgumentException("스터디 DTO 필드 유효성 검사 오류");
        }

        existingStudy.setStudyName(studyDto.getStudyName());
        existingStudy.setDescription(studyDto.getDescription());
        existingStudy.setStatus(studyDto.getStatus());
        existingStudy.setCategory(studyDto.getCategory());
        existingStudy.setLocation(studyDto.getLocation());
        existingStudy.setMaxMember(studyDto.getMaxMember());
        existingStudy.setCurrentMember(studyDto.getCurrentMember());
        existingStudy.setPhotoPath(studyDto.getPhotoPath());

        studyRepository.save(existingStudy);
    }

    // 스터디 삭제
    @Transactional
    public void deleteStudy(int studyId) {
        Study study = studyRepository.findByStudyId(studyId);
        UserPrincipalDetails currentUser = getAuthentication();
        validateStudyAndUser(study, currentUser);

        // 관련 StudyMember 삭제
        List<StudyMember> studyMembers = studyMemberRepository.findByStudy(study);
        studyMemberRepository.deleteAll(studyMembers);

        studyRepository.delete(study);
    }

    // 스터디 신청 목록 가져오기
    @Transactional(readOnly = true)
    public List<StudyJoinDto> getStudyJoinsByStudyId(int studyId) throws IllegalAccessException {
        UserPrincipalDetails currentUser = getAuthentication();
        String currentUserId = currentUser.getUsername();

        Study study = studyRepository.findByStudyId(studyId);
        if (!study.getCreatorId().getUserId().equals(currentUserId)) {
            throw new IllegalAccessException("접근 권한이 없습니다.");
        }

        List<StudyJoin> studyJoins = studyJoinRepository.findByStudy(studyRepository.findByStudyId(studyId));
        return studyJoins.stream()
                .map(studyJoin -> new StudyJoinDto(
                        studyJoin.getJoinId(),
                        studyJoin.getStudy(),
                        studyJoin.getUser(),
                        studyJoin.getJoinDate(),
                        studyJoin.getIntroduction(),
                        studyJoin.getRole()))
                .collect(Collectors.toList());
    }

    // 스터디 신청
    @Transactional
    public void applyStudy(int studyId, StudyJoinDto studyJoinDto, BindingResult result) {
        if (result.hasErrors()) {
            throw new IllegalArgumentException("자기소개는 최소 10글자 이상이어야 합니다.");
        }

        UserPrincipalDetails currentUser = getAuthentication();
        Study study = studyRepository.findByStudyId(studyId);

        if (study == null) {
            throw new IllegalArgumentException("해당 스터디를 찾을 수 없습니다.");
        }

        // 신청이 이미 존재하는지 확인
        if (studyJoinRepository.findByStudyAndUser(study, currentUser.getUser()).isPresent()) {
            throw new IllegalArgumentException("이미 신청되었습니다.");
        }

        studyJoinDto.setStudy(study);
        studyJoinDto.setUser(currentUser.getUser());
        studyJoinDto.setJoinDate(new Date());

        // DTO를 엔티티로 변환
        StudyJoin studyJoin = studyJoinDto.toEntity();
        studyJoinRepository.save(studyJoin);
    }

    // 스터디 신청 승인
    @Transactional
    public void acceptStudyJoin(Long joinId) {
        StudyJoin studyJoin = studyJoinRepository.findById(joinId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 신청 ID입니다."));

        Study study = studyJoin.getStudy();
        if (study.getCurrentMember() >= study.getMaxMember()) {
            throw new IllegalStateException("이미 스터디의 최대 정원입니다.");
        }

        StudyMember studyMember = new StudyMember();
        studyMember.setStudy(studyJoin.getStudy());
        studyMember.setUser(studyJoin.getUser());
        studyMember.setJoinDate(new Date());
        studyMember.setRole(studyJoin.getRole());

        studyMemberRepository.save(studyMember);
        studyJoinRepository.delete(studyJoin);

        // 현재 멤버 수 증가
        study.setCurrentMember(study.getCurrentMember() + 1);
        studyRepository.save(study);
    }

    // 스터디 신청 거절
    @Transactional
    public void rejectStudyJoin(Long joinId) {
        StudyJoin studyJoin = studyJoinRepository.findById(joinId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 신청 ID입니다."));

        studyJoinRepository.delete(studyJoin);
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

    // 사진 유효성 검사 및 저장
    @Transactional
    public void validatePhoto(MultipartFile photo, StudyDto studyDto) throws IOException {
        if (photo != null && !photo.isEmpty()) {
            if (!FileUploadUtil.isExtensionValid(photo)) {
                throw new IOException("허용되는 파일 형식은 jpg, jpeg, png입니다.");
            }

            String fileName = StringUtils.cleanPath(photo.getOriginalFilename());
            FileUploadUtil.saveFile(uploadDir, fileName, photo);
            studyDto.setPhotoPath("/img/study-logo/" + fileName);
        }
    }

    // DB값 DTO로 가져오기
    public StudyDto buildStudyDto(Study study) {
        StudyDto studyDto = new StudyDto();
        studyDto.setStudyId(study.getStudyId());
        studyDto.setCreatorId(study.getCreatorId());
        studyDto.setStudyName(study.getStudyName());
        studyDto.setDescription(study.getDescription());
        studyDto.setStatus(study.getStatus());
        studyDto.setCategory(study.getCategory());
        studyDto.setLocation(study.getLocation());
        studyDto.setMaxMember(study.getMaxMember());
        studyDto.setCurrentMember(study.getCurrentMember());
        studyDto.setPhotoPath(study.getPhotoPath());
        return studyDto;
    }

    // 스터디와 사용자 유효성 검사
    private void validateStudyAndUser(Study study, UserPrincipalDetails currentUser) {
        if (study == null) {
            throw new IllegalArgumentException("해당 스터디를 찾을 수 없습니다.");
        }

        if (currentUser == null) {
            throw new IllegalArgumentException("회원을 찾을 수 없습니다.");
        }

        if (!study.getCreatorId().getUserId().equals(currentUser.getUser().getUserId())) {
            throw new IllegalStateException("스터디에 대한 권한이 없습니다.");
        }
    }

    // 로그인 된 사용자 정보 가져오기
    public UserPrincipalDetails getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (UserPrincipalDetails) authentication.getPrincipal();
    }

    // 스터디 조회
    public Study findByStudyId(int studyId) {
        return studyRepository.findByStudyId(studyId);
    }
}
