package com.thxforservice.member.services;

import com.thxforservice.member.constants.Authority;
import lombok.RequiredArgsConstructor;
import com.thxforservice.member.MemberUtil;
import com.thxforservice.member.controllers.RequestJoin;
import com.thxforservice.member.controllers.RequestUpdate;
import com.thxforservice.member.entities.Member;
import com.thxforservice.member.exceptions.MemberNotFoundException;
import com.thxforservice.member.repositories.MemberRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberSaveService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final MemberUtil memberUtil;
    /**
     * 회원 가입 처리
     *
     * @param form
     */
    public void save(RequestJoin form) {
        Member member = new ModelMapper().map(form, Member.class);
        String hash = passwordEncoder.encode(form.getPassword()); // BCrypt 해시화
        member.setPassword(hash);

        save(member);
    }

    /**
     * 회원정보 수정
     * @param form
     */
    public void save(RequestUpdate form) {
        Member member = memberUtil.getMember();
        String email = member.getEmail();
        member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        String password = form.getPassword();
        String mobile = form.getMobile();
        if (StringUtils.hasText(mobile)) {
            mobile = mobile.replaceAll("\\D", "");
        }

        member.setUserName(form.getUserName());
        member.setMobile(mobile);

        if (StringUtils.hasText(password)) {
            String hash = passwordEncoder.encode(password);
            member.setPassword(hash);
        }

        save(member);
    }

    public void save(Member member) {

        // 휴대전화번호 숫자만 기록
        String mobile = member.getMobile();
        if (StringUtils.hasText(mobile)) {
            mobile = mobile.replaceAll("\\D", "");
            member.setMobile(mobile);
        }

        String gid = member.getGid();
        gid = StringUtils.hasText(gid) ? gid : UUID.randomUUID().toString();
        member.setGid(gid);

        memberRepository.saveAndFlush(member);

        // 권한 추가, 수정 E
    }
}
