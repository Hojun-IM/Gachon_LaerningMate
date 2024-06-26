package com.gachon.learningmate.controller;

import com.gachon.learningmate.data.dto.UserPrincipalDetails;
import com.gachon.learningmate.data.entity.Study;
import com.gachon.learningmate.service.FavoriteService;
import com.gachon.learningmate.service.StudyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController extends BaseController {

    private final StudyService studyService;
    private final FavoriteService favoriteService;

    @Autowired
    public HomeController(StudyService studyService, FavoriteService favoriteService) {
        this.studyService = studyService;
        this.favoriteService = favoriteService;
    }

    // 홈 화면 불러오기
    @GetMapping("/home")
    public String home(Model model) {
        addUserInfoToModel(model);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<Study> favoriteStudies = null;
        List<Study> recentStudies = studyService.findRecentStudy(10);

        if (authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String && authentication.getPrincipal().equals("anonymousUser"))) {
            UserPrincipalDetails userDetails = (UserPrincipalDetails) authentication.getPrincipal();
            String userId = userDetails.getUsername();
            favoriteStudies = favoriteService.findFavoriteStudyByUserId(userId);

            if (favoriteStudies == null || favoriteStudies.isEmpty()) {
                favoriteStudies = recentStudies;
            }
        } else {
            favoriteStudies = recentStudies;
        }

        model.addAttribute("favoriteStudies", favoriteStudies);
        model.addAttribute("recentStudies", recentStudies);
        return "home";
    }

}
