package com.gachon.learningmate.data.dto;

import com.gachon.learningmate.data.entity.User;
import com.gachon.learningmate.data.entity.UserType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDto {

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,20}$", message = "ID는 6~20자의 영문자와 숫자의 조합이어야 합니다.")
    @NotBlank(message = "ID는 필수 항목입니다.")
    private String userId;

    @Pattern(regexp="(?=.*[0-9])(?=.*[a-z])(?=.*\\W)(?=\\S+$).{8,16}", message = "비밀번호는 8~16자의 영문자, 숫자, 특수 문자의 조합이어야 합니다.")
    @NotBlank(message = "비밀번호는 필수 항목입니다.")
    private String password;

    @Pattern(regexp = "^[가-힣a-zA-Z]{2,30}$", message = "이름은 한글 또는 영문자만 가능합니다.")
    @NotBlank(message = "이름은 필수 항목입니다.")
    private String username;

    @Pattern(regexp = "^[\\w.-]+@gachon\\.ac\\.kr$", message = "가천대학교 이메일을 사용해주세요.")
    @NotBlank(message = "이메일은 필수 항목입니다.")
    private String email;

    @NotBlank(message = "인증 코드는 필수 항목입니다.")
    private String verificationCode="";

    @NotBlank(message = "생년월일은 필수 항목입니다.")
    @Pattern(regexp = "^\\d{4}/\\d{2}/\\d{2}$", message = "생일은 yyyy/MM/dd 형식으로 입력해야 합니다.")
    private String birth;

    @Builder.Default
    private UserType type = UserType.Member;

    public User toEntity() {
        User user = new User();
        user.setUserId(userId);
        user.setPassword(password);
        user.setUsername(username);
        user.setEmail(email);
        user.setType(type);
        user.setBirth(convertStringToDate(birth));
        return user;
    }

    private Date convertStringToDate(String birth) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        try {
            return dateFormat.parse(birth);
        } catch (ParseException e) {
            throw new IllegalArgumentException("생일은 yyyy/MM/dd 형식으로 입력해야 합니다.");
        }
    }
}
