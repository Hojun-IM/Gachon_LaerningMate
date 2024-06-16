package com.gachon.learningmate.controller;

import com.gachon.learningmate.data.dto.UserPrincipalDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController extends BaseController {

    // 홈 화면 불러오기
    @GetMapping("/home")
    public String home(Model model) {
        addUserInfoToModel(model);
        return "home";
    }

}
