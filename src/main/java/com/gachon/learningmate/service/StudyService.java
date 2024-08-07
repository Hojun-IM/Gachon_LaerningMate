package com.gachon.learningmate.service;

import com.gachon.learningmate.data.dto.StudyDto;
import com.gachon.learningmate.data.dto.StudyJoinDto;
import com.gachon.learningmate.data.dto.UserPrincipalDetails;
import com.gachon.learningmate.data.entity.*;
import com.gachon.learningmate.data.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
public class StudyService {

    // 사진 업로드 경로
    @Value("${cloud.aws.s3.bucket}")
    private String bucketUrl;

    private final StudyRepository studyRepository;
    private final StudyJoinRepository studyJoinRepository;
    private final StudyMemberRepository studyMemberRepository;
    private final UserRepository userRepository;
    private final FavoriteRepository favoriteRepository;
    private final UserService userService;
    private final S3Service s3Service;

    @Autowired
    public StudyService(StudyRepository studyRepository, StudyJoinRepository studyJoinRepository,
                        StudyMemberRepository studyMemberRepository, UserRepository userRepository,
                        FavoriteRepository favoriteRepository, UserService userService,
                        S3Service s3Service) {
        this.studyRepository = studyRepository;
        this.studyJoinRepository = studyJoinRepository;
        this.studyMemberRepository = studyMemberRepository;
        this.userRepository = userRepository;
        this.favoriteRepository = favoriteRepository;
        this.userService = userService;
        this.s3Service = s3Service;
    }

    // 전체 스터디 조회
    public Page<Study> findAllStudy(Pageable pageable) {
        return studyRepository.findAll(pageable);
    }

    // 내 스터디 조회
    public Page<Study> findStudyByUserId(String userId, Pageable pageable) {
        User user = userRepository.findByUserId(userId);
        return studyRepository.findStudyByCreatorId(user, pageable);
    }

    // 최근 등록된 스터디 조회
    public List<Study> findRecentStudy(int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit);
        List<Study> recentStudies = studyRepository.findAllByOrderByCreatedAtDesc(pageRequest).getContent();
        return recentStudies;
    }

    // 스터디 검색
    public Page<Study> searchStudiesByName(String keyword, Pageable pageable) {
        return studyRepository.findByStudyNameContainingIgnoreCase(keyword, pageable);
    }

    // 스터디 가입된 멤버 조회
    public List<StudyMember> findStudyMemberByStudyId(int studyId) {
        Study study = studyRepository.findByStudyId(studyId);
        return studyMemberRepository.findByStudy(study);
    }

    // 스터디 생성
    @Transactional
    public void createStudy(StudyDto studyDto, MultipartFile photo) throws IOException {
        UserPrincipalDetails userPrincipalDetails = userService.getAuthentication();
        studyDto.setCreatorId(userPrincipalDetails.getUser());

        // 기본 사진 경로 설정
        if (photo != null && !photo.isEmpty()) {
            String fileUrl = s3Service.saveFile(photo);
            studyDto.setPhotoPath(fileUrl);
        } else {
            studyDto.setPhotoPath("https://learning-mate.s3.amazonaws.com/study/img/default-study.jpg");
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
    public void updateStudy(StudyDto studyDto, MultipartFile photo) throws IOException {
        Study existingStudy = studyRepository.findByStudyId(studyDto.getStudyId());
        UserPrincipalDetails principalDetails = userService.getAuthentication();
        validateStudyAndUser(existingStudy, principalDetails);

        // 이미지 경로 확인
        if (photo != null && !photo.isEmpty()) {
            // 기본 이미지가 아닌 경우 삭제
            if (existingStudy.getPhotoPath() != null && existingStudy.getPhotoPath().equals(bucketUrl + "/default-study.jpg")) {
                s3Service.deleteFile(existingStudy.getPhotoPath());
            }
            String fileUrl = s3Service.saveFile(photo);
            studyDto.setPhotoPath(fileUrl);
        } else {
            studyDto.setPhotoPath(existingStudy.getPhotoPath());
        }

        existingStudy.updateFromDto(studyDto);
        studyRepository.save(existingStudy);
    }

    // 스터디 삭제
    @Transactional
    public void deleteStudy(int studyId) {
        Study study = studyRepository.findByStudyId(studyId);
        UserPrincipalDetails currentUser = userService.getAuthentication();
        validateStudyAndUser(study, currentUser);

        List<Favorite> favorites = favoriteRepository.findByStudy(study);
        favoriteRepository.deleteAll(favorites);
        List<StudyMember> studyMembers = studyMemberRepository.findByStudy(study);
        studyMemberRepository.deleteAll(studyMembers);
        studyRepository.delete(study);
    }

    // 스터디 신청 목록 가져오기
    @Transactional(readOnly = true)
    public List<StudyJoinDto> getStudyJoinByStudyId(int studyId) throws IllegalAccessException {
        UserPrincipalDetails currentUser = userService.getAuthentication();
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

        UserPrincipalDetails currentUser = userService.getAuthentication();
        Study study = studyRepository.findByStudyId(studyId);

        if (study == null) {
            throw new IllegalArgumentException("해당 스터디를 찾을 수 없습니다.");
        }

        // 신청이 이미 존재하는지 확인
        if (studyJoinRepository.findByStudyAndUser(study, currentUser.getUser()).isPresent()) {
            throw new IllegalArgumentException("이미 신청되었습니다.");
        }

        // 이미 스터디의 멤버인지 확인
        if (studyMemberRepository.findByStudyAndUser(study, currentUser.getUser()).isPresent()) {
            throw new IllegalArgumentException("이미 가입된 스터디입니다.");
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
    public Map<String, String> validateHandling(BindingResult result) {
        Map<String, String> validatorResult = new HashMap<>();
        for (FieldError error : result.getFieldErrors()) {
            String errorKey = String.format("error_%s", error.getField());
            validatorResult.put(errorKey, error.getDefaultMessage());
        }
        return validatorResult;
    }

    // 스터디 멤버 삭제
    @Transactional
    public void removeStudyMember(int studyId, String memberId) {
        Study study = studyRepository.findByStudyId(studyId);
        User member = userRepository.findByUserId(memberId);

        StudyMember studyMember = studyMemberRepository.findByStudyAndUser(study, member)
                .orElseThrow(() -> new IllegalArgumentException("해당 멤버를 찾을 수 없습니다."));

        studyMemberRepository.delete(studyMember);
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
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }

        if (!study.getCreatorId().getUserId().equals(currentUser.getUser().getUserId())) {
            throw new IllegalStateException("스터디에 대한 권한이 없습니다.");
        }
    }

    // 스터디 조회
    public Study findByStudyId(int studyId) {
        return studyRepository.findByStudyId(studyId);
    }
}
