package com.gachon.learningmate.data.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "user")
public class User {

    @Id
    @Column(name = "user_id", length = 20)
    private String userId;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 30)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    // Date 타입 필드가 DB의 날짜 타입 컬럼과 매핑되는 방식 날짜만으로 지정
    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date birth;

    // 열거형 이름을 문자열로 저장 (Mebmer, Admin)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType type;

    // 생성시 자동으로 현재 날짜 시간 저장
    @CreationTimestamp
    // Date 타입 필드가 DB의 날짜 타입 컬럼과 매핑되는 방식 날짜와 시간으로 지정
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "reg_date", updatable = false)
    private Date regDate;

}