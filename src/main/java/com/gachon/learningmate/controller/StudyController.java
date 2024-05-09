package com.gachon.learningmate.controller;

import com.gachon.learningmate.data.repository.StudyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

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

    @GetMapping("/register-study")
    public String showRegisterStudy() {
        return "registerStudy";
    }
}
