package com.thxforservice.mypage.controllers;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.thxforservice.member.entities.Employee;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class RequestProfile {
    private String email;

    @NotBlank
    private String username;

    @Size(min = 8)
    private String password;

    private String confirmPassword;

    private String mobile;

    private String authority;

    private String zonecode; // 우편번호
    private String address; // 주소
    private String addressSub; // 상세 주소

    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate birthdate; // 생년월일

    private String status;

    // 학생 정보

    private Long studentNo; // 학번
    private String department; // 학과
    private String grade; // 학년
    private Long professor; // 지도 교수 기본키

    // 교직원 정보

    private Long empNo; // 사번
    private String subject; // 담당 과목

    private String introduction;
}