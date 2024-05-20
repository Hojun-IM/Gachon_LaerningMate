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
import org.springframework.security.core.Authentication;
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

    // 스터디 생성 페이지 출력
    @GetMapping("/create")
    public String showCreateStudy(Model model) {
        // 현재 로그인 된 유저 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipalDetails userPrincipalDetails = (UserPrincipalDetails) authentication.getPrincipal();

        model.addAttribute("username", userPrincipalDetails.getUserRealName());
        model.addAttribute("email", userPrincipalDetails.getUserEamil());
        return "createStudy";
    }

    // 스터디 생성
    @PostMapping("/create")
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

    // 스터디 목록 페이지 출력
    @GetMapping
    public String showStudyList(Model model, @RequestParam(defaultValue = "1") int page) {
        // 한 페이지에 보여줄 아이템 수
        int pageSize = 12;
        // PageRequest는 페이지 번호가 0부터 시작하므로, 사용자 입력에서 1을 빼줍니다.
        // 하지만 페이지가 1 이하일 경우, 0으로 설정합니다.
        int pageIndex = (page < 1) ? 0 : page - 1;
        Page<Study> studyPage = studyServices.findAllStudy(PageRequest.of(pageIndex, pageSize));

        // 현재 페이지의 스터디 목록
        model.addAttribute("studies", studyPage.getContent());
        // 현재 페이지 번호 (1부터 시작)
        model.addAttribute("currentPage", page);
        // 전체 페이지 수
        model.addAttribute("totalPages", studyPage.getTotalPages());
        // 다음 페이지가 있는지의 여부
        model.addAttribute("hasNext", studyPage.hasNext());
        // 이전 페이지가 있는지의 여부
        model.addAttribute("hasPrevious", studyPage.hasPrevious());
        // 다음 페이지 번호
        model.addAttribute("nextPage", page + 1);
        // 이전 페이지 번호 (1보다 클 경우만 -1 적용)
        model.addAttribute("prevPage", (page > 1) ? page - 1 : 1);

        // 페이지 목록 생성
        List<PageItem> pages = PageItem.createPageItems(page, studyPage.getTotalPages());
        model.addAttribute("pages", pages);

        return "study"; // 스터디 목록을 보여줄 뷰 이름
    }

    @GetMapping("/info")
    public String showStudyInfo(Model model, @RequestParam int studyId) {
        // 현재 로그인 된 유저 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserId = authentication.getName();

        Study study = studyRepository.findByStudyId(studyId);

        model.addAttribute("study", study);
        model.addAttribute("isCreator", study.getCreatorId().getUserId().equals(currentUserId));

        return "studyInfo";
    }

    @PostMapping("/delete")
    public String deleteStudy(@RequestParam int studyId) {
        // 현재 로그인 된 유저 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipalDetails userPrincipalDetails = (UserPrincipalDetails) authentication.getPrincipal();

        Study study = studyRepository.findByStudyId(studyId);
        studyServices.deleteStudy(study, userPrincipalDetails);

        return "redirect:/study";
    }

    @GetMapping("/update")
    public String showUpdateStudy(Model model, @RequestParam int studyId) {
        Study study = studyRepository.findByStudyId(studyId);
        StudyDto studyDto = studyServices.buildStudyDto(study);

        model.addAttribute("studyDto", studyDto);
        return "studyUpdate";
    }
}
