package com.thxforservice.member.entities;

import com.thxforservice.global.entities.BaseEntity;
import com.thxforservice.member.constants.Authority;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseEntity {
    @Id
    @GeneratedValue
    private Long seq;

    @Column(length=45, nullable = false)
    private String gid; // 파일

    @Column(length=65, unique = true, nullable = false)
    private String email;

    @Column(length=65, nullable = false)
    private String password;

    @Column(length=40, nullable = false)
    private String userName;

    @Column(length=15, nullable = false)
    private String mobile;

    private String studentNo; // 학번
    private String grade; // 학년
    private String department; // 학과
    private String professor; // 지도 교수

    private String address; // 주소
    private String addressSub; // 나머지 주소

    private String subject; // 담당 과목

    private String self_introduction; // 상담사 소개

    private Double rating_score; // 평가점수

    private LocalDate birthdate; //생년월일

    @Enumerated(EnumType.STRING)
    @Column(length=10)
    private Authority authority;
}