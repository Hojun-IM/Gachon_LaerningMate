package com.gachon.learningmate.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController extends BaseController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        addUserInfoToModel(model);
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            if (statusCode == 404) {
                model.addAttribute("errorMessage", "페이지를 찾을 수 없습니다.");
            } else if (statusCode == 500) {
                model.addAttribute("errorMessage", "서버에 오류가 발생했습니다.");
            } else {
                model.addAttribute("errorMessage", "잘못된 접근입니다.");
            }
        } else {
            model.addAttribute("errorMessage", "잘못된 접근입니다.");
        }

        return "customError";
    }

}
