package com.gachon.learningmate.service;

import com.gachon.learningmate.data.dto.RegisterDto;
import com.gachon.learningmate.data.entity.UserType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class Case {

    @Autowired
    RegisterService registerService;

    @Test
    public void test() {
        RegisterDto registerDto = new RegisterDto();

        registerDto.setUserId("hojun8094");
        registerDto.setUsername("임호준");
        registerDto.setPassword("Popcorn$7");
        registerDto.setType(UserType.Member);
        registerDto.setBirth("2000/04/11");
        registerDto.setEmail("hojun8094@gachon.ac.kr");
        registerService.register(registerDto);

    }
}
