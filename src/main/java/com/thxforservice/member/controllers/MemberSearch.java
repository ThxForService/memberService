package com.thxforservice.member.controllers;

import lombok.Data;
import com.thxforservice.global.CommonSearch;

import java.util.List;

@Data
public class MemberSearch extends CommonSearch {
    private int limit = 20; // 페이지당 갯수
    private String sort; // 정렬 조건
    private List<String> email;
    private List<String> authority;
    private Long studentNo; // 상담사 일때만 (마이페이지가)
}