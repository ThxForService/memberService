package com.thxforservice.member.entities;

import com.thxforservice.file.entities.FileInfo;
import com.thxforservice.global.entities.BaseEntity;
import com.thxforservice.member.constants.Authority;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Member extends BaseEntity {
    @Id
    @GeneratedValue
    private Long memberSeq;

    @Column(length=65, unique = true, nullable = false)
    private String email;

    @Column(length=40, nullable = false)
    private String username; // 이름

    @Column(length=65, nullable = false)
    private String password; // 비밀번호

    @Column(length=15, nullable = false)
    private String mobile; // 전화번호

    private LocalDate birthdate; // 생년월일

    @Enumerated(EnumType.STRING)
    @Column(length=10, nullable = false)
    private Authority authority;

    @Column(length=10)
    private String zonecode; // 우편번호

    @Column(length=100)
    private String address; // 주소

    @Column(length=100)
    private String addressSub; // 나머지 주소

    @Column(length=45, nullable = false)
    private String gid; // 파일

    @Transient
    private FileInfo profileImage;
}