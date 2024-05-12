package com.gachon.learningmate.data.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Table(name = "study")
public class Study {

    @Id
    // 기본 키 값을 DB에 자동 생성
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int studyId;

    // 한 명의 User가 여러 Study를 생성할 수 있음
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User creatorId;

    @Column(nullable = false, length = 100)
    private String studyName;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StudyStatus status;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(nullable = false, length = 20)
    private String location;

    @Column(nullable = false)
    private int maxMember;

    @Column(nullable = false)
    private int currentMember = 1;

    // 생성시 자동으로 날짜 시간 저장
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    // 업데이트시 자동으로 날짜 시간 저장
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;

    // 사진 경로
    @Column(nullable = false)
    private String photoPath;
}