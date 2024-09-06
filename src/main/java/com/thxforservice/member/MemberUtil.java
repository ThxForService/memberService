package com.thxforservice.member;

import lombok.RequiredArgsConstructor;
import com.thxforservice.member.constants.Authority;
import com.thxforservice.member.entities.Member;
import com.thxforservice.member.repositories.MemberRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MemberUtil {

    private final MemberRepository repository;

    public boolean isLogin() {
        return getMember() != null;
    }

    public boolean isAdmin() {
        if (isLogin()) {

            return getMember().getAuthority() == Authority.ADMIN;
        }
        return false;
    }

    public Member getMember() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof MemberInfo memberInfo) {

            Member member = repository.findByEmail(memberInfo.getEmail()).orElse(null);
            memberInfo.setMember(member);

            return member;
        }

        return null;
    }
}