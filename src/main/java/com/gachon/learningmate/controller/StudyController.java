package com.gachon.learningmate.controller;

import com.gachon.learningmate.data.dto.UserPrincipalDetails;
import com.gachon.learningmate.data.entity.Study;
import com.gachon.learningmate.data.repository.StudyRepository;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class StudyController {

    private final StudyRepository studyRepository;

    @Autowired
    public StudyController(StudyRepository studyRepository) {
        this.studyRepository = studyRepository;
    }

    @GetMapping("/study")
    public String showStudyList() {
        return "study";
    }

    @GetMapping("/study-create")
    public String showCreateStudy(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipalDetails userPrincipalDetails = (UserPrincipalDetails) authentication.getPrincipal();

        model.addAttribute("username", userPrincipalDetails.getUserRealName());
        model.addAttribute("email", userPrincipalDetails.getUserEamil());
        return "createStudy";
    }

    @PostMapping("/study-create")
    public String createStudy(@ModelAttribute("study") Study study) {

        return"redirect:/study";
    }
}
