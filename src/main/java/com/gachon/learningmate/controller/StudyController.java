package com.gachon.learningmate.controller;

import com.gachon.learningmate.config.PageItem;
import com.gachon.learningmate.data.dto.StudyDto;
import com.gachon.learningmate.data.dto.StudyJoinDto;
import com.gachon.learningmate.data.dto.UserPrincipalDetails;
import com.gachon.learningmate.data.entity.Study;
import com.gachon.learningmate.data.repository.StudyJoinRepository;
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
public class StudyController extends BaseController {

    private final StudyServices studyServices;

    @Autowired
    public StudyController(StudyServices studyServices) {
        this.studyServices = studyServices;
    }

    // 스터디 생성 페이지
    @GetMapping("/create")
    public String showCreateStudy(Model model) {
        addUserInfoToModel(model);
        UserPrincipalDetails userPrincipalDetails = studyServices.getAuthentication();
        model.addAttribute("username", userPrincipalDetails.getUserRealName());
        model.addAttribute("email", userPrincipalDetails.getUserEamil());
        return "createStudy";
    }

    // 스터디 생성
    @PostMapping("/create")
    public String createStudy(@RequestParam(value = "photo", required = false) MultipartFile photo, @Valid StudyDto studyDto, BindingResult result, Model model) {
        addUserInfoToModel(model);
        try {
            studyServices.createStudy(studyDto, photo, result);
        } catch (IOException | IllegalArgumentException e) {
            UserPrincipalDetails userPrincipalDetails = studyServices.getAuthentication();
            model.addAttribute("username", userPrincipalDetails.getUserRealName());
            model.addAttribute("email", userPrincipalDetails.getUserEamil());
            model.addAttribute("error", e.getMessage());
            Map<String, String> validatorResult = studyServices.validateHandling(result);
            for (String key : validatorResult.keySet()) {
                model.addAttribute(key, validatorResult.get(key));
            }
            return "createStudy";
        }

        return "redirect:/study";
    }

    // 스터디 목록
    @GetMapping
    public String showStudyList(Model model, @RequestParam(defaultValue = "1") int page) {
        addUserInfoToModel(model);

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
        addUserInfoToModel(model);
        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();
        Study study = studyServices.findByStudyId(studyId);

        model.addAttribute("study", study);
        model.addAttribute("isCreator", study.getCreatorId().getUserId().equals(currentUserId));

        return "studyInfo";
    }

    // 스터디 삭제
    @PostMapping("/delete")
    public String deleteStudy(Model model, @RequestParam int studyId) {
        addUserInfoToModel(model);
        studyServices.deleteStudy(studyId);
        return "redirect:/study";
    }

    // 스터디 수정 페이지
    @GetMapping("/update")
    public String showUpdateStudy(Model model, @RequestParam int studyId) {
        addUserInfoToModel(model);
        Study study = studyServices.findByStudyId(studyId);
        StudyDto studyDto = studyServices.buildStudyDto(study);

        model.addAttribute("studyDto", studyDto);
        return "updateStudy";
    }

    // 스터디 수정
    @PostMapping("/update")
    public String updateStudy(@RequestParam int studyId, @RequestParam(value = "photo", required = false) MultipartFile photo, @Valid StudyDto studyDto, BindingResult result, Model model) {
        addUserInfoToModel(model);
        try {
            studyDto.setStudyId(studyId);
            studyServices.updateStudy(studyDto, photo, result);
        } catch (IOException | IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            Map<String, String> validatorResult = studyServices.validateHandling(result);
            for (String key : validatorResult.keySet()) {
                model.addAttribute(key, validatorResult.get(key));
            }
            return "updateStudy";
        }

        return "redirect:/study/info?studyId=" + studyDto.getStudyId();
    }

    // 스터디 신청 폼
    @GetMapping("/participate")
    public String showApplyStudy(@RequestParam int studyId, @RequestParam(value = "photo", required = false) MultipartFile photo, StudyDto studyDto, Model model) {
        addUserInfoToModel(model);
        UserPrincipalDetails userPrincipalDetails = studyServices.getAuthentication();
        studyDto.setCreatorId(userPrincipalDetails.getUser());
        studyDto.setStudyId(studyId);

        if (photo == null || photo.isEmpty()) {
            Study existingStudy = studyServices.findByStudyId(studyId);
            studyDto.setPhotoPath(existingStudy.getPhotoPath());
        } else {
            try {
                studyServices.validatePhoto(photo, studyDto);
            } catch (IOException e) {
                model.addAttribute("error_photoPath", e.getMessage());
                model.addAttribute("studyDto", studyDto);
                return "updateStudy";
            }
        }

        model.addAttribute("studyId", studyId);
        model.addAttribute("username", userPrincipalDetails.getUserRealName());
        model.addAttribute("email", userPrincipalDetails.getUserEamil());

        return "applyStudy";
    }

    // 스터디 신청
    @PostMapping("/participate")
    public String applyStudy(@RequestParam int studyId, @Valid StudyJoinDto studyJoinDto, BindingResult bindingResult, Model model) {
        addUserInfoToModel(model);

        model.addAttribute("studyId", studyId);
        UserPrincipalDetails userPrincipalDetails = studyServices.getAuthentication();
        model.addAttribute("username", userPrincipalDetails.getUserRealName());
        model.addAttribute("email", userPrincipalDetails.getUserEamil());

        try {
            studyServices.applyStudy(studyId, studyJoinDto, bindingResult);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error_apply", e.getMessage());
            model.addAttribute("studyJoinDto", studyJoinDto);
            return "applyStudy";
        }

        return "redirect:/study";
    }

    // 스터디 신청 목록
    @GetMapping("/apply/list")
    public String showApplyStudyList(Model model, @RequestParam int studyId) {
        addUserInfoToModel(model);
        try {
            List<StudyJoinDto> studyJoinList = studyServices.getStudyJoinsByStudyId(studyId);
            model.addAttribute("studyJoinList", studyJoinList);
        } catch (IllegalAccessException e) {
            model.addAttribute("error_apply", e.getMessage());
            return "applyStudyList";
        }
        return "applyStudyList";
    }

    // 스터디 신청 승인
    @PostMapping("/apply/accept")
    public String acceptStudyJoin(@RequestParam long joinId, @RequestParam int studyId, Model model) {
        addUserInfoToModel(model);
        try {
            studyServices.acceptStudyJoin(joinId);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error_apply", e.getMessage());
            return "redirect:/study/apply/list?studyId=" + studyId;
        }
        return "redirect:/study/apply/list?studyId=" + studyId;
    }

    // 스터디 신청 거절
    @PostMapping("/apply/reject")
    public String rejectStudyJoin(@RequestParam long joinId, @RequestParam int studyId, Model model) {
        addUserInfoToModel(model);
        try {
            studyServices.rejectStudyJoin(joinId);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error_apply", e.getMessage());
            return "redirect:/study/apply/list?studyId=" + studyId;
        }
        return "redirect:/study/apply/list?studyId=" + studyId;
    }
}
