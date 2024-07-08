package com.gachon.learningmate.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        // 오류 메시지 인코딩
        String errorMessage = URLEncoder.encode(exception.getMessage(), StandardCharsets.UTF_8);

        // 예외 메시지를 쿼리 매개변수로 추가
        response.sendRedirect("/login?error=true&message=" + errorMessage);
    }

}
