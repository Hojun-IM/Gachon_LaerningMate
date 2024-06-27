package com.gachon.learningmate.config;

import com.gachon.learningmate.handler.CustomAuthenticationFailureHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String[] PUBLIC_MATCHERS = {
            "/", "/home", "/register", "/login", "/register/send-verification", "/register/verify-code", "/study", "/study/create", "/study/info", "/study/participate", "/error", "/study/info**"
    };

    private static final String[] STATIC_RESOURCES = {
            "static/**", "/css/**", "/js/**", "/img/**"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authorizeRequests) ->
                        authorizeRequests
                                // 로그인 없이 접근 허용
                                .requestMatchers(PUBLIC_MATCHERS).permitAll()
                                // 정적 파일 접근 허용
                                .requestMatchers(STATIC_RESOURCES).permitAll()
                                // 그 외 모든 요청은 인증 필요
                                .anyRequest().authenticated()
                )
                .formLogin((formLogin) ->
                        formLogin
                                // 사용자 정의 로그인 페이지
                                .loginPage("/login")
                                .defaultSuccessUrl("/home", true)
                                .failureHandler(customAuthenticationFailureHandler())
                                // 로그인 페이지 접근 허용
                                .permitAll()
                )
                .logout((logout) ->
                        logout
                                // 로그아웃 성공 시 리다이렉트할 페이지
                                .logoutSuccessUrl("/home")
                                // 로그인 페이지 접근 허용
                                .permitAll()
                )
                .csrf((csrfConfig) ->
                        // CSRF 보호 기능 비활성화
                        csrfConfig.disable()
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationFailureHandler customAuthenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler();
    }
}
