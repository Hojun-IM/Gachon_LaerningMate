package com.gachon.learningmate.data.dto;

import com.gachon.learningmate.data.entity.Study;
import com.gachon.learningmate.data.entity.StudyJoin;
import com.gachon.learningmate.data.entity.StudyJoinRole;
import com.gachon.learningmate.data.entity.User;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyJoinDto {

    private Long joinId;

    private Study study;

    private User user;

    private Date joinDate;

    @Size(min = 10, message = "자기소개는 최소 10글자 이상이어야 합니다.")
    private String introduction;

    private StudyJoinRole role;

    public StudyJoin toEntity() {
        StudyJoin studyJoin = new StudyJoin();
        studyJoin.setStudy(this.study);
        studyJoin.setUser(this.user);
        studyJoin.setIntroduction(this.introduction);
        studyJoin.setJoinDate(this.joinDate);
        studyJoin.setRole(StudyJoinRole.MEMBER);
        return studyJoin;
    }
}
