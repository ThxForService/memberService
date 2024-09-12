package com.thxforservice.member.controllers;

import com.thxforservice.global.ListData;
import com.thxforservice.global.rests.JSONData;
import com.thxforservice.member.entities.Member;
import com.thxforservice.member.services.MemberAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor

@RequestMapping("/admin")
public class MemberAdminController {

    private final MemberAdminService service;

    @DeleteMapping("/delete/{seq}")
    public JSONData delete(@PathVariable("seq") Long seq) {
        Member member = service.deleteMember(seq);
        return new JSONData(member);
    }

    // admin 회원정보 조회
    @GetMapping("/list")
    public JSONData list(MemberSearch search) {
        ListData<Member> data = service.getList(search);
        return new JSONData(data);
    }

}
