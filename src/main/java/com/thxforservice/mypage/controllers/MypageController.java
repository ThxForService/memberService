package com.thxforservice.mypage.controllers;

import com.thxforservice.global.Utils;
import com.thxforservice.global.exceptions.BadRequestException;
import com.thxforservice.global.rests.JSONData;
import com.thxforservice.member.MemberUtil;
import com.thxforservice.member.entities.Member;
import com.thxforservice.member.services.MemberSaveService;
import com.thxforservice.mypage.validators.ProfileUpdateValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MypageController {

    private final MemberSaveService memberSaveService;
    private final ProfileUpdateValidator profileUpdateValidator;
    private final MemberUtil memberUtil;
    private final Utils utils;

    @PatchMapping("/profile")
    public JSONData profileUpdate(@RequestBody @Valid RequestProfile form, Errors errors) {
        profileUpdateValidator.validate(form, errors);
        System.out.println("수정처리");
        if (errors.hasErrors()) {
            throw new BadRequestException(utils.getErrorMessages(errors));
        }
        Member member = memberSaveService.save(form);
        System.out.println("수정처리");


        // 회원 정보를 입력 후  새롭게 추가
        return new JSONData(member);


    }

}
