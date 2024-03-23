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
@Table(name = "user")
public class User {

    @Id
    @Column(name = "userId", length = 20)
    private String userId;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 30)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date birth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType type;

    // 생성시 자동으로 날짜 시간 저장
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "regDate", updatable = false)
    private Date regDate;

}
