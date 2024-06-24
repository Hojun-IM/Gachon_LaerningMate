package com.gachon.learningmate.controller;

import com.gachon.learningmate.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/favorite")
public class FavoriteController extends BaseController {

    private final FavoriteService favoriteService;

    @Autowired
    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    // 스터디 즐겨찾기 추가
    @PostMapping("/add")
    public String addFavorite(@RequestParam String userId, @RequestParam int studyId, Model model) {
        addUserInfoToModel(model);

        boolean success = favoriteService.addFavorite(userId, studyId);
        if (success)
            model.addAttribute("message", "즐겨찾기에 추가되었습니다.");
        else
            model.addAttribute("error", "즐겨찾기 추가에 실패했습니다.");
        return "redirect:/study/info?studyId=" + studyId;
    }

    // 스터디 즐겨찾기 삭제
    @PostMapping("/remove")
    public String removeFavorite(@RequestParam String userId, @RequestParam int studyId, Model model) {
        addUserInfoToModel(model);

        boolean success = favoriteService.removeFavorite(userId, studyId);
        if (success)
            model.addAttribute("message", "즐겨찾기에서 삭제되었습니다.");
        else
            model.addAttribute("error", "즐겨찾기 삭제에 실패했습니다.");
        return "redirect:/study/info?studyId=" + studyId;
    }
}
