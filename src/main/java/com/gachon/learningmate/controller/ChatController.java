package com.gachon.learningmate.controller;

import com.gachon.learningmate.data.entity.User;
import com.gachon.learningmate.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/chat")
@Slf4j
public class ChatController extends BaseController {

    private final UserService userService;

    @Autowired
    public ChatController(UserService userService) {
        this.userService = userService;
    }

    // 전체 채팅 불러오기
    @GetMapping
    public String chat(Model model) {
        addUserInfoToModel(model);
        String currentUserName = userService.getAuthentication().getUserRealName();
        model.addAttribute("username", currentUserName);

        log.info("@ChatController, chat GET()");
        return "chat";
    }

}
