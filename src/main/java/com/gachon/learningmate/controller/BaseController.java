package com.gachon.learningmate.controller;

import com.gachon.learningmate.data.dto.UserPrincipalDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@Controller
public abstract class BaseController {

    protected void addUserInfoToModel(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String && authentication.getPrincipal().equals("anonymousUser"))) {
            UserPrincipalDetails userDetails = (UserPrincipalDetails) authentication.getPrincipal();
            model.addAttribute("username", userDetails.getUserRealName());
        }
    }
}
