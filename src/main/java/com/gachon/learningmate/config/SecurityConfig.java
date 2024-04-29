package com.gachon.learningmate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authrizeRequest) ->
                        authrizeRequest
                                .requestMatchers("/", "/home", "/login", "/registerFirst", "/register/send-verification", "/register/verify-code").permitAll()  // 로그인 없이 접근 허용
                                .requestMatchers("/registerSecond").authenticated()              // 인증된 사용자만 접근 허용
                                .requestMatchers("/css/**", "/js/**", "/img/**").permitAll() // 정적 파일 접근 허용
                                .anyRequest().authenticated()                                      // 그 외 모든 요청은 인증 필요
                )
                .formLogin((formLogin) ->
                        formLogin
                            .loginPage("/login")  // 사용자 정의 로그인 페이지
                            .permitAll()          // 로그인 페이지 접근 허용
                )
                .logout((logout) ->
                        logout
                            .logoutSuccessUrl("/home")  // 로그아웃 성공 시 리다이렉트할 페이지
                            .permitAll()                // 로그인 페이지 접근 허용
                )
                .csrf((csrfConfig) ->
                        csrfConfig.disable()// CSRF 보호 기능 비활성화
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
