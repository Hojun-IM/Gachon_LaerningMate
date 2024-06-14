package com.gachon.learningmate.data.dto;

import com.gachon.learningmate.data.entity.Study;
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
}
