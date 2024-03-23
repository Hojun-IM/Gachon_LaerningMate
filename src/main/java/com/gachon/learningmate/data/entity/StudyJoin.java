package com.gachon.learningmate.data.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Table(name = "study_join")
public class StudyJoin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "join_id")
    private Long joinId;

    @ManyToOne
    @JoinColumn(name = "study_id", referencedColumnName = "study_id")
    private Study study;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "join_date", nullable = false, updatable = false)
    private Date joinDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StudyJoinRole role;
}
