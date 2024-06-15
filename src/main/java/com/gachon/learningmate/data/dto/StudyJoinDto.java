package com.gachon.learningmate.data.dto;

import com.gachon.learningmate.data.entity.Study;
import com.gachon.learningmate.data.entity.StudyJoin;
import com.gachon.learningmate.data.entity.StudyJoinRole;
import com.gachon.learningmate.data.entity.User;
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
