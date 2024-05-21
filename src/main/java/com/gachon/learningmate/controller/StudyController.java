package com.gachon.learningmate.controller;

import com.gachon.learningmate.config.PageItem;
import com.gachon.learningmate.data.dto.StudyDto;
import com.gachon.learningmate.data.dto.UserPrincipalDetails;
import com.gachon.learningmate.data.entity.Study;
import com.gachon.learningmate.data.repository.StudyRepository;
import com.gachon.learningmate.service.StudyServices;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/study")
public class StudyController {

    private final StudyServices studyServices;
    private final StudyRepository studyRepository;

    @Autowired
    public StudyController(StudyServices studyServices, StudyRepository studyRepository) {
        this.studyServices = studyServices;
        this.studyRepository = studyRepository;
    }

    // 스터디 생성 페이지
    @GetMapping("/create")
    public String showCreateStudy(Model model) {
        UserPrincipalDetails userPrincipalDetails = studyServices.getAuthentication();

        // 유저 정보 전달
        model.addAttribute("username", userPrincipalDetails.getUserRealName());
        model.addAttribute("email", userPrincipalDetails.getUserEamil());
        return "createStudy";
    }

    // 스터디 생성
    @PostMapping("/create")
    public String createStudy(@RequestParam(value = "photo", required = false) MultipartFile photo, @Valid StudyDto studyDto, BindingResult result, Model model) {
        UserPrincipalDetails userPrincipalDetails = studyServices.getAuthentication();
        studyDto.setCreatorId(userPrincipalDetails.getUser());

        // 사진 업로드 유효성 검사
        try {
            studyServices.validatePhoto(photo, studyDto);

            // 스터디 DTO 필드 유효성 검사
            if (result.hasErrors()) {
                model.addAttribute("username", userPrincipalDetails.getUserRealName());
                model.addAttribute("email", userPrincipalDetails.getUserEamil());
                // 에러 메시지 저장
                Map<String, String> validatorResult = studyServices.validateHandling(result);
                for (String key : validatorResult.keySet()) {
                    model.addAttribute(key, validatorResult.get(key));
                }
                return "createStudy";
            }

        } catch (IOException e) {
            model.addAttribute("username", userPrincipalDetails.getUserRealName());
            model.addAttribute("email", userPrincipalDetails.getUserEamil());
            model.addAttribute("error_photoPath", e.getMessage());
            return "createStudy";
        }

        studyServices.createStudy(studyDto);
        return "study";
    }

    // 스터디 목록
    @GetMapping
    public String showStudyList(Model model, @RequestParam(defaultValue = "1") int page) {
        // 한 페이지에 보여줄 아이템 수
        int pageSize = 12;
        int pageIndex = (page < 1) ? 0 : page - 1;
        Page<Study> studyPage = studyServices.findAllStudy(PageRequest.of(pageIndex, pageSize));

        // 페이지 정보
        model.addAttribute("studies", studyPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", studyPage.getTotalPages());
        model.addAttribute("hasNext", studyPage.hasNext());
        model.addAttribute("hasPrevious", studyPage.hasPrevious());
        model.addAttribute("nextPage", page + 1);
        model.addAttribute("prevPage", (page > 1) ? page - 1 : 1);

        // 페이지 목록 생성
        List<PageItem> pages = PageItem.createPageItems(page, studyPage.getTotalPages());
        model.addAttribute("pages", pages);

        return "study";
    }

    // 스터디 상세 정보
    @GetMapping("/info")
    public String showStudyInfo(Model model, @RequestParam int studyId) {
        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();

        Study study = studyRepository.findByStudyId(studyId);

        model.addAttribute("study", study);
        model.addAttribute("isCreator", study.getCreatorId().getUserId().equals(currentUserId));

        return "studyInfo";
    }

    // 스터디 삭제
    @PostMapping("/delete")
    public String deleteStudy(@RequestParam int studyId) {
        UserPrincipalDetails userPrincipalDetails = studyServices.getAuthentication();

        Study study = studyRepository.findByStudyId(studyId);
        studyServices.deleteStudy(study, userPrincipalDetails);

        return "redirect:/study";
    }

    // 스터디 수정 페이지
    @GetMapping("/update")
    public String showUpdateStudy(Model model, @RequestParam int studyId) {
        Study study = studyRepository.findByStudyId(studyId);
        StudyDto studyDto = studyServices.buildStudyDto(study);

        model.addAttribute("studyDto", studyDto);
        System.out.println("studyDto.getPhotoPath() = " + studyDto.getPhotoPath());
        return "updateStudy";
    }

    // 스터디 수정
    @PostMapping("/update")
    public String updateStudy(@RequestParam int studyId, @RequestParam(value = "photo", required = false) MultipartFile photo, @Valid StudyDto studyDto, BindingResult result, Model model) {
        UserPrincipalDetails userPrincipalDetails = studyServices.getAuthentication();
        studyDto.setCreatorId(userPrincipalDetails.getUser());
        studyDto.setStudyId(studyId);

        // 기존 photoPath를 설정
        if (photo == null || photo.isEmpty()) {
            Study existingStudy = studyRepository.findByStudyId(studyId);
            studyDto.setPhotoPath(existingStudy.getPhotoPath());
        } else {
            // 사진 유효성 검사 및 업로드 처리
            try {
                studyServices.validatePhoto(photo, studyDto);
            } catch (IOException e) {
                model.addAttribute("error_photoPath", e.getMessage());
                model.addAttribute("studyDto", studyDto);
                return "updateStudy";
            }
        }

        // DTO 유효성 검사
        if (result.hasErrors()) {
            model.addAttribute("studyDto", studyDto);
            // 에러 메시지 저장
            Map<String, String> validatorResult = studyServices.validateHandling(result);
            for (String key : validatorResult.keySet()) {
                model.addAttribute(key, validatorResult.get(key));
            }
            return "updateStudy";
        }

        studyServices.updateStudy(studyDto);
        return "redirect:/study/info?studyId=" + studyDto.getStudyId();
    }

}
