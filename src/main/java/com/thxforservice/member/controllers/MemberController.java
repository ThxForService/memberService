package com.thxforservice.member.controllers;

import com.thxforservice.global.ListData;
import com.thxforservice.global.Utils;
import com.thxforservice.global.exceptions.BadRequestException;
import com.thxforservice.global.rests.JSONData;
import com.thxforservice.member.MemberInfo;
import com.thxforservice.member.MemberUtil;
import com.thxforservice.member.entities.Employee;
import com.thxforservice.member.entities.Member;
import com.thxforservice.member.jwt.TokenProvider;
import com.thxforservice.member.services.MemberInfoService;
import com.thxforservice.member.services.MemberSaveService;
import com.thxforservice.member.validators.JoinValidator;
import com.thxforservice.mypage.controllers.RequestProfile;
import com.thxforservice.mypage.validators.ProfileUpdateValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.sql.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Member", description = "회원 인증 API")
@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class MemberController {

    private final JoinValidator joinValidator;
    private final MemberSaveService saveService;
    private final TokenProvider tokenProvider;
    private final ProfileUpdateValidator updateValidator;
    private final MemberInfoService memberInfoService;
    private final MemberUtil memberUtil;
    private final Utils utils;

    @Operation(summary = "인증(로그인)한 회원 정보 조회")
    @ApiResponse(responseCode = "200", description = "조회 가능 범위<br>학생 : 학과, 지도교수, 주소, 휴대폰 번호, 이메일<br>상담사/교수 : 담당 과목, 휴대폰 번호, 이메일" )
    // 로그인한 회원 정보 조회
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public JSONData info(@AuthenticationPrincipal MemberInfo memberInfo) {
        Member member = memberInfo.getMember();

        return new JSONData(member);
    }

    @Operation(summary = "회원가입")
    @ApiResponse(responseCode = "201", description = "회원가입 성공시 201")
    @Parameters({
            @Parameter(name="email", required = true, description = "이메일"),
            @Parameter(name="password", required = true, description = "비밀번호"),
            @Parameter(name="confirmPassword", required = true, description = "비밀번호 확인"),
            @Parameter(name="username", required = true, description = "사용자명"),
            @Parameter(name="mobile", description = "휴대전화번호, 형식 검증 있음"),
            @Parameter(name="agree", required = true, description = "회원가입약관 동의")
    })
    @PostMapping
    public ResponseEntity join(@RequestBody @Valid RequestJoin form, Errors errors) {

        joinValidator.validate(form, errors);

        if (errors.hasErrors()) {
            errors.getAllErrors().forEach(System.out::println);
            throw new BadRequestException(utils.getErrorMessages(errors));
        }

        saveService.save(form);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "인증 및 토큰 발급", description = "인증 성공시 JWT 토큰 발급")
    @ApiResponse(responseCode = "201", headers = @Header(name="application/json"), description = "data이 발급 받은 토큰")

    @Parameters({
        @Parameter(name="email", required = true, description = "이메일"),
            @Parameter(name="password", required = true, description = "비밀번호")
    })
    @PostMapping("/token")
    public JSONData token(@RequestBody @Valid RequestLogin form, Errors errors) {
        System.out.println("form: " + form);
        if (errors.hasErrors()) {
           throw new BadRequestException(utils.getErrorMessages(errors));
        }

        String token = tokenProvider.createToken(form.getEmail(), form.getPassword());
        System.out.println("token: " + token);
        return new JSONData(token);
    }

    @Operation(summary = "회원정보리스트")
    @ApiResponse(responseCode = "200")
    // 회원정보 리스트 조회
    @GetMapping("/list")
    public JSONData list() {
        List<Member> members = memberInfoService.getMembers(); // 페이지 형태로 구현
        return new JSONData(members);
    }

    /* 상담사 목록 조회*/
    @Operation(summary = "상담사 목록 조회")
    @ApiResponse(responseCode = "200")
    @GetMapping("/counselors")
    public JSONData getCounselorList(MemberSearch search) {
        ListData<Employee> counselors = memberInfoService.getCounselorList(search);

        return new JSONData(counselors);
    }

    @Operation(summary = "지도교수 목록 키워드 검색")
    @GetMapping("/professors")
    @PreAuthorize("permitAll()")
    public JSONData professors(@RequestParam(name = "skey", required = false) String skey) {
        List<Employee> items = memberInfoService.getProfessors(skey);

        return new JSONData(items); // 검색을 통해서 목록이 나오면 교수를 선택
    }



    @PatchMapping
    public JSONData update(@Valid @RequestBody RequestProfile form, Errors errors) {

        updateValidator.validate(form, errors);

        if(errors.hasErrors()) {
            throw new BadRequestException(utils.getErrorMessages(errors));
        }

       Member member = saveService.save(form);


        return new JSONData(member);
    }

}
