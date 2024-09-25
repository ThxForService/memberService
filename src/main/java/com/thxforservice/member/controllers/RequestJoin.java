package com.thxforservice.member.controllers;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.thxforservice.member.constants.Authority;
import com.thxforservice.member.constants.Status;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestJoin {

    /* 회원 공통 S */
    private String gid = UUID.randomUUID().toString();

    @NotBlank
    @Email
    private String email;

    @NotBlank @Size(min=8)
    private String password;

    @NotBlank
    private String confirmPassword;

    @NotBlank
    private String username;

    @NotBlank
    private String mobile;

    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate birthDate;

    @NotBlank
    private String authority; // 회원 유형 - STUDENT, COUNSELLOR

    private String zonecode; // 우편번호
    private String address; // 주소
    private String addressSub; // 나머지 주소
    private String status; // 상담사와 학생이 서로 다른 값을 가지고 있다.
    /* 회원 공통 E */

    /* 학생 추가 정보 S */
    private Long studentNo;
    private String grade; // 학년
    private String department; // 학과
    private Long professor; // 지도교수
    /* 학생 추가 정보 E */

    /* 상담사 추가 정보 S */
    private Long empNo; // 사번
    private String introduction; // 상담사 소개
    private String subject; // 담당과목
    private Double rating; // 평가 점수
    /* 상담사 추가 정보 E */

    @AssertTrue
    private boolean agree;
}