package com.gachon.learningmate.config;

import com.gachon.learningmate.data.dto.UserPrincipalDetails;
import com.gachon.learningmate.data.entity.User;
import com.gachon.learningmate.service.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserAuthenticatonProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;

    @Autowired
    public UserAuthenticatonProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 사용자 입력 로그인 정보
        String userId = authentication.getName();
        String password = authentication.getCredentials().toString();

        // 사용자 정보 가져오기
        UserPrincipalDetails userPrincipalDetails = (UserPrincipalDetails) userDetailsService.loadUserByUsername(userId);

        // DB에 저장된 비밀번호와 비교
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String dbPassword = userPrincipalDetails.getPassword();

        // 사용자 정보가 일치하지 않을 경우
        if(!passwordEncoder.matches(password, dbPassword)) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        // 사용자 정보가 존재하지 않는 경우
        User user = userPrincipalDetails.getUser();
        if (user == null) {
            throw new BadCredentialsException("존재하지 않는 사용자 정보입니다.");
        }

        // 사용자 정보가 존재하고 일치하는 경우
        return new UsernamePasswordAuthenticationToken(userPrincipalDetails, null, userPrincipalDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}
