package com.gachon.learningmate.data.dto;

import com.gachon.learningmate.data.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserPrincipalDetails implements UserDetails {

    private final User user;

    public UserPrincipalDetails(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    // 사용자 이름 반환
    public String getUserRealName() {
        return user.getUsername();
    }

    // 사용자 이메일 반환
    public String getUserEmail() {
        return user.getEmail();
    }

    // 권한 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("Member"));
    }

    // 사용자 id 반환
    @Override
    public String getUsername() {
        return user.getUserId();
    }

    // 사용자 비밀번호 반환
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    // 계정 만료 여부 반환
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정 잠금 여부 반환
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 비밀번호 만료 여부 반환
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 계정 사용 가능 여부 반환
    @Override
    public boolean isEnabled() {
        return true;
    }
}
