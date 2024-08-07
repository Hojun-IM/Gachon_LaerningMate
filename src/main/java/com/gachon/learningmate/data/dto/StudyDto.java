package com.gachon.learningmate.data.dto;

import com.gachon.learningmate.data.entity.Study;
import com.gachon.learningmate.data.entity.StudyStatus;
import com.gachon.learningmate.data.entity.User;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyDto {

    private int studyId;

    private User creatorId;

    @Size(min = 2, max = 15, message = "스터디명은 2글자에서 15글자 사이여야 합니다.")
    private String studyName;

    @Size(min = 10, message = "스터디 설명은 최소 10글자 이상이어야 합니다.")
    private String description;

    @Builder.Default
    private StudyStatus status = StudyStatus.Open;

    @NotBlank(message = "스터디 카테고리는 필수 항목입니다.")
    private String category;

    @Size(min = 2, message = "활동 장소는 최소 2글자 이상이어야 합니다.")
    private String location;

    @Min(value = 1, message = "최소 멤버 수는 1명입니다.")
    @Max(value = 50, message = "최대 멤버 수는 50명입니다.")
    private int maxMember;

    @Builder.Default
    @Min(value = 1, message = "현재 멤버 수는 최소 1명입니다.")
    private int currentMember = 1;

    private String photoPath;

    public Study toEntity() {
        Study study = new Study();
        study.setStudyId(studyId);
        study.setCreatorId(creatorId);
        study.setStudyName(studyName);
        study.setDescription(description);
        study.setStatus(status);
        study.setCategory(category);
        study.setLocation(location);
        study.setMaxMember(maxMember);
        study.setCurrentMember(currentMember);
        study.setPhotoPath(photoPath);
        return study;
    }
}
