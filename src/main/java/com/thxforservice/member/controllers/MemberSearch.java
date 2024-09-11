package com.thxforservice.member.controllers;

import lombok.Data;
import com.thxforservice.global.CommonSearch;

@Data
public class MemberSearch extends CommonSearch {
    private int limit = 20; // 페이지당 갯수
    private String sort; // 정렬 조건
}