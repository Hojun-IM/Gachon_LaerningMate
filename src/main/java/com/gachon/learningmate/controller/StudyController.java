package com.gachon.learningmate.controller;

import com.gachon.learningmate.data.dto.StudyDto;
import com.gachon.learningmate.data.dto.UserPrincipalDetails;
import com.gachon.learningmate.data.entity.User;
import com.gachon.learningmate.service.StudyServices;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

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
    public String showStudyList() {
        studyServices.findAllStudy();
        return "study";
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
    public String createStudy(@Valid StudyDto studyDto, BindingResult result, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipalDetails userPrincipalDetails = (UserPrincipalDetails) authentication.getPrincipal();

        User currentUser = userPrincipalDetails.getUser();
        studyDto.setCreatorId(currentUser);

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
        return "redirect:/study";
    }

}
