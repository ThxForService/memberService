package com.thxforservice.member.controllers;

import com.thxforservice.global.ListData;
import com.thxforservice.global.rests.JSONData;
import com.thxforservice.member.MemberInfo;
import com.thxforservice.member.entities.Member;
import com.thxforservice.member.services.MemberAdminService;
import com.thxforservice.member.services.MemberInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberAdminController {

    private final MemberAdminService service;
    private final MessageSource messageSource;
    private final MemberInfoService memberInfoService;

    @Operation(summary = "회원 삭제", description = "회원 고유 번호로 회원을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "성공적으로 회원 삭제"),
    })
    @DeleteMapping("/delete/{memberSeq}")
    public ResponseEntity<Void> delete(@PathVariable("memberSeq") Long memberSeq) {

        service.deleteMember(memberSeq);

        return ResponseEntity.noContent().build();
    }

    // admin 회원 목록 조회
    @Operation(summary = "회원 목록 조회", description = "items - 조회된 회원목록, pagination - 페이징 기초 데이터", method = "GET")
    @ApiResponse(responseCode = "200")
    @Parameters({
            @Parameter(name="page", description = "페이지 번호", example = "1"),
            @Parameter(name="limit", description = "한페이지당 레코드 갯수", example = "20"),
            @Parameter(name="sopt", description = "검색옵션", example = "ALL"),
            @Parameter(name="skey", description = "검색키워드"),
    })
    @GetMapping("/list")
    public JSONData list(MemberSearch search) {
        ListData<Member> data = service.getList(search);
        return new JSONData(data);
    }

    // admin 회원정보 조회
    @Operation(summary = "회원 한명 조회", method = "GET")
    @ApiResponse(responseCode = "200")
    @Parameter(name="email", required = true, description = "경로변수, 회원 이메일(로그인시 아이디로 활용)")
    @GetMapping("/info/{email}")
    public JSONData info(@PathVariable("email") String email) {

        MemberInfo memberInfo = (MemberInfo)memberInfoService.loadUserByUsername(email);
        Member member = memberInfo.getMember();

        return new JSONData(member);
    }

}
