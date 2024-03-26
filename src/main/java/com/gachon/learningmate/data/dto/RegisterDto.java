package com.gachon.learningmate.data.dto;

import com.gachon.learningmate.data.entity.UserType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDto {

    @NotBlank(message = "ID는 필수 항목입니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,20}$", message = "ID는 6~20자의 영문자와 숫자의 조합이어야 합니다.")
    private String userId;

    @NotBlank(message = "비밀번호는 필수 항목입니다.")
    @Pattern(regexp="(?=.*[0-9])(?=.*[a-z])(?=.*\\W)(?=\\S+$).{8,16}", message = "비밀번호는 8~16자의 영문자, 숫자, 특수 문자의 조합이어야 합니다.")
    private String password;

    @NotBlank(message = "이름은 필수 항목입니다.")
    @Pattern(regexp = "^[가-힣a-zA-Z]{3,30}$", message = "이름은 한글 또는 영문자만 가능합니다.")
    private String username;

    @NotBlank(message = "이메일은 필수 항목입니다.")
    @Pattern(regexp = "^[\\w.-]+@gachon\\.ac\\.kr$", message = "가천대학교 이메일을 사용해주세요.")
    private String email;

    @NotBlank(message = "생년월일은 필수 항목입니다.")
    @DateTimeFormat(pattern = "yyyy/MM/dd")
    private Date birth;

    private UserType type;
}
