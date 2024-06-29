package com.gachon.learningmate.controller;

import com.gachon.learningmate.config.PageItem;
import com.gachon.learningmate.data.dto.StudyDto;
import com.gachon.learningmate.data.dto.StudyJoinDto;
import com.gachon.learningmate.data.dto.UserPrincipalDetails;
import com.gachon.learningmate.data.entity.Study;
import com.gachon.learningmate.data.entity.StudyMember;
import com.gachon.learningmate.service.FavoriteService;
import com.gachon.learningmate.service.StudyService;
import com.gachon.learningmate.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/study")
public class StudyController extends BaseController {

    private final StudyService studyService;
    private final FavoriteService favoriteService;
    private final UserService userService;

    @Autowired
    public StudyController(StudyService studyService, FavoriteService favoriteService, UserService userService) {
        this.studyService = studyService;
        this.favoriteService = favoriteService;
        this.userService = userService;
    }

    // 스터디 생성 페이지
    @GetMapping("/create")
    public String showCreateStudy(Model model) {
        addUserInfoToModel(model);
        UserPrincipalDetails userPrincipalDetails = userService.getAuthentication();
        model.addAttribute("username", userPrincipalDetails.getUserRealName());
        model.addAttribute("email", userPrincipalDetails.getUserEamil());
        return "createStudy";
    }

    // 스터디 생성
    @PostMapping("/create")
    public String createStudy(@RequestParam(value = "photo", required = false) MultipartFile photo, @Valid StudyDto studyDto, BindingResult result, Model model) {
        addUserInfoToModel(model);
        try {
            studyService.createStudy(studyDto, photo);
        } catch (IOException | IllegalArgumentException e) {
            UserPrincipalDetails userPrincipalDetails = userService.getAuthentication();
            model.addAttribute("username", userPrincipalDetails.getUserRealName());
            model.addAttribute("email", userPrincipalDetails.getUserEamil());
            model.addAttribute("error", e.getMessage());
            Map<String, String> validatorResult = studyService.validateHandling(result);
            validatorResult.forEach(model::addAttribute);
            return "createStudy";
        }

        return "redirect:/study";
    }

    // 스터디 목록
    @GetMapping
    public String showAllStudy(@RequestParam(defaultValue = "1") int page, Model model) {
        addUserInfoToModel(model);

        // 한 페이지에 보여줄 아이템 수
        int pageSize = 12;
        int pageIndex = Math.max(page - 1, 0);
        Page<Study> studyPage = studyService.findAllStudy(PageRequest.of(pageIndex, pageSize));

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

    // 내 스터디 목록
    @GetMapping("/my")
    public String showMyStudy(@RequestParam(defaultValue = "1") int page, Model model) {
        addUserInfoToModel(model);
        String userId = userService.getAuthentication().getUsername();

        // 한 페이지에 보여줄 아이템 수
        int pageSize = 12;
        int pageIndex = Math.max(page - 1, 0);
        Page<Study> studyPage = studyService.findStudyByUserId(userId, PageRequest.of(pageIndex, pageSize));

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
    public String showStudyInfo(@RequestParam int studyId, Model model) {
        addUserInfoToModel(model);
        String currentUserId = null;
        boolean isFavorite = false;
        boolean isCreator = false;
        Study study = studyService.findByStudyId(studyId);

        try {
            UserPrincipalDetails currentUser = userService.getAuthentication();
            currentUserId = currentUser.getUsername();
            isFavorite = favoriteService.isFavorite(currentUserId, studyId);
            isCreator = study.getCreatorId().getUserId().equals(currentUserId);
        } catch (RuntimeException e) {
            // 로그인이 되어 있지 않은 경우
            currentUserId = null;
        }

        model.addAttribute("study", study);
        model.addAttribute("currentUserId", currentUserId != null ? currentUserId : "");
        model.addAttribute("isFavorite", isFavorite);
        model.addAttribute("isCreator", isCreator);

        return "studyInfo";
    }

    // 스터디 삭제
    @PostMapping("/delete")
    public String deleteStudy(@RequestParam int studyId, Model model) {
        addUserInfoToModel(model);
        studyService.deleteStudy(studyId);
        return "redirect:/study";
    }

    // 스터디 수정 페이지
    @GetMapping("/update")
    public String showUpdateStudyForm(@RequestParam int studyId, Model model) {
        addUserInfoToModel(model);
        Study study = studyService.findByStudyId(studyId);
        StudyDto studyDto = studyService.buildStudyDto(study);

        model.addAttribute("studyDto", studyDto);
        return "updateStudy";
    }

    // 스터디 수정
    @PostMapping("/update")
    public String updateStudy(@RequestParam int studyId, @RequestParam(value = "photo", required = false) MultipartFile photo, @Valid StudyDto studyDto, BindingResult result, Model model) {
        addUserInfoToModel(model);
        try {
            studyDto.setStudyId(studyId);
            studyService.updateStudy(studyDto, photo);
        } catch (IOException | IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            Map<String, String> validatorResult = studyService.validateHandling(result);
            validatorResult.forEach(model::addAttribute);
            return "updateStudy";
        }

        return "redirect:/study/info?studyId=" + studyDto.getStudyId();
    }

    // 스터디 신청 폼
    @GetMapping("/apply")
    public String showApplyStudyForm(@RequestParam int studyId, @RequestParam(value = "photo", required = false) MultipartFile photo, StudyDto studyDto, Model model) {
        addUserInfoToModel(model);
        UserPrincipalDetails userPrincipalDetails = userService.getAuthentication();
        studyDto.setCreatorId(userPrincipalDetails.getUser());
        studyDto.setStudyId(studyId);

        if (photo == null || photo.isEmpty()) {
            Study existingStudy = studyService.findByStudyId(studyId);
            studyDto.setPhotoPath(existingStudy.getPhotoPath());
        } else {
            try {
                studyService.validatePhoto(photo, studyDto);
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
    @PostMapping("/apply")
    public String applyStudy(@RequestParam int studyId, @Valid StudyJoinDto studyJoinDto, BindingResult bindingResult, Model model) {
        addUserInfoToModel(model);

        UserPrincipalDetails userPrincipalDetails = userService.getAuthentication();
        model.addAttribute("username", userPrincipalDetails.getUserRealName());
        model.addAttribute("email", userPrincipalDetails.getUserEamil());
        model.addAttribute("studyId", studyId);

        try {
            studyService.applyStudy(studyId, studyJoinDto, bindingResult);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error_apply", e.getMessage());
            model.addAttribute("studyJoinDto", studyJoinDto);
            return "redirect:/apply";
        }

        return "redirect:/study";
    }

    // 스터디 신청 목록
    @GetMapping("/apply/list")
    public String showStudyJoinList(@RequestParam int studyId, Model model) {
        addUserInfoToModel(model);
        try {
            List<StudyJoinDto> studyJoinList = studyService.getStudyJoinByStudyId(studyId);
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
            studyService.acceptStudyJoin(joinId);
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
            studyService.rejectStudyJoin(joinId);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error_apply", e.getMessage());
            return "redirect:/study/apply/list?studyId=" + studyId;
        }
        return "redirect:/study/apply/list?studyId=" + studyId;
    }

    // 스터디 검색
    @GetMapping("/search")
    public String searchStudy(@RequestParam String keyword, @RequestParam(defaultValue = "1") int page, Model model){
        addUserInfoToModel(model);

        int pageSize = 12;
        int pageIndex = Math.max(page - 1, 0);
        Page<Study> studyPage = studyService.searchStudiesByName(keyword, PageRequest.of(pageIndex, pageSize));

        model.addAttribute("studies", studyPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", studyPage.getTotalPages());
        model.addAttribute("hasNext", studyPage.hasNext());
        model.addAttribute("hasPrevious", studyPage.hasPrevious());
        model.addAttribute("nextPage", page + 1);
        model.addAttribute("prevPage", (page > 1) ? page - 1 : 1);

        List<PageItem> pages = PageItem.createPageItems(page, studyPage.getTotalPages());
        model.addAttribute("pages", pages);

        return "study";
    }

    // 스터디 멤버 조회
    @GetMapping("/member")
    public String showStudyMember(@RequestParam int studyId, Model model) {
        addUserInfoToModel(model);

        List<StudyMember> studyMemberList = studyService.findStudyMemberByStudyId(studyId);
        studyMemberList.forEach(member -> {
            member.setFormattedJoinDate(new SimpleDateFormat("yyyy-MM-dd").format(member.getJoinDate()));
        });
        model.addAttribute("studyMemberList", studyMemberList);

        return "studyMemberList";
    }
}
