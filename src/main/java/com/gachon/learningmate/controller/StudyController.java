package com.gachon.learningmate.controller;

import com.gachon.learningmate.data.dto.StudyDto;
import com.gachon.learningmate.data.dto.UserPrincipalDetails;
import com.gachon.learningmate.data.entity.Study;
import com.gachon.learningmate.service.StudyServices;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
public class StudyController {

    private final StudyServices studyServices;

    @Autowired
    public StudyController(StudyServices studyServices) {
        this.studyServices = studyServices;
    }

    // 스터디 목록 페이지 출력
    @GetMapping("/study")
    public String showStudyList(Model model, @RequestParam(defaultValue = "0") int page) {
        int pageSize = 12; // 한 페이지에 보여줄 아이템 수
        Page<Study> studyPage = studyServices.findAllStudy(PageRequest.of(page, pageSize));
        model.addAttribute("study", studyPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", studyPage.getTotalPages());
        model.addAttribute("totalItems", studyPage.getTotalElements());
        return "study"; // 스터디 목록을 보여줄 뷰 이름
    }

    // 스터디 생성 페이지 출력
    @GetMapping("/study-create")
    public String showCreateStudy(Model model) {
        // 현재 로그인 된 유저 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipalDetails userPrincipalDetails = (UserPrincipalDetails) authentication.getPrincipal();

        model.addAttribute("username", userPrincipalDetails.getUserRealName());
        model.addAttribute("email", userPrincipalDetails.getUserEamil());
        return "createStudy";
    }

    // 스터디 생성
    @PostMapping("/study-create")
    public String createStudy(@RequestParam(value = "photo", required = false) MultipartFile photo, @Valid StudyDto studyDto, BindingResult result, Model model) {
        // 현재 로그인 된 유저 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipalDetails userPrincipalDetails = (UserPrincipalDetails) authentication.getPrincipal();
        studyDto.setCreatorId(userPrincipalDetails.getUser());

        // 사진 업로드 유효성 검사
        try {
            studyServices.validatePhoto(photo, studyDto);

        } catch (IOException e) {
            model.addAttribute("username", userPrincipalDetails.getUserRealName());
            model.addAttribute("email", userPrincipalDetails.getUserEamil());
            model.addAttribute("error_photoPath", e.getMessage());
            return "createStudy";
        }

        // 스터디 DTO 필드 유효성 검사
        if (result.hasErrors()) {
            model.addAttribute("username", userPrincipalDetails.getUserRealName());
            model.addAttribute("email", userPrincipalDetails.getUserEamil());

            Map<String, String> validatorResult = studyServices.validateHandling(result);
            for (String key : validatorResult.keySet()) {
                model.addAttribute(key, validatorResult.get(key));
            }
            return "createStudy";
        }

        studyServices.createStudy(studyDto);
        return "study";
    }

}
