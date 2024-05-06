package com.gachon.learningmate.controller;

import com.gachon.learningmate.data.dto.UserPrincipalDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    // 홈 화면 불러오기
    @GetMapping("/home")
    public String home(Model model) {
        // Authentication 객체 불러오기
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // UserPrincipalDetails에서 저장된 사용자명 불러오기
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof UserPrincipalDetails) {
            // 유저명 가져오기
            UserPrincipalDetails userPrincipalDetails = (UserPrincipalDetails) auth.getPrincipal();
            model.addAttribute("username", userPrincipalDetails.getUserRealName());
        }

        return "home";
    }
    
    
}
