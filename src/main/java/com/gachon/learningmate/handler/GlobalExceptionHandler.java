package com.gachon.learningmate.handler;

import com.gachon.learningmate.exception.DuplicateEmailException;
import com.gachon.learningmate.exception.DuplicateUserIdException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 중복 아이디 처리
    @ExceptionHandler(DuplicateUserIdException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleDuplicateUserIdException(DuplicateUserIdException e) {
        return e.getMessage();
    }

    // 중복 이메일 처리
    @ExceptionHandler(DuplicateEmailException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleDuplicateEmailException(DuplicateEmailException e) {
        return e.getMessage();
    }

    // 잘못된 값 전달 처리
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgumentException(IllegalArgumentException e) {
        return e.getMessage();
    }
}
