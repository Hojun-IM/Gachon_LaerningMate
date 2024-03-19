package com.gachon.learningmate.data.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "study")
public class StudyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int study_id;

    @Column(nullable = false)
    private String creator_id;

    @Column(nullable = false, length = 100)
    private String study_name;

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
    private int max_member;

    @Column(nullable = false)
    private int current_member = 1;

    @CreationTimestamp
    @Column(nullable = false)
    private Timestamp created_at;

    @UpdateTimestamp
    @Column(nullable = false)
    private Timestamp updated_at;

}
