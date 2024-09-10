package com.thxforservice.member;

import com.thxforservice.member.constants.Authority;
import com.thxforservice.member.entities.Member;
import com.thxforservice.member.repositories.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberUtil {

    private final MemberRepository repository;

    public boolean isLogin() {
        return getMember() != null;
    }

    public boolean isAdmin() {
        return isLogin() && getMember().getAuthority() == Authority.ADMIN;
    }

    public boolean isCounselor() {
        return isLogin() && getMember().getAuthority() == Authority.COUNSELOR;
    }

    public boolean isStudent() {
        return isLogin() && getMember().getAuthority() == Authority.STUDENT;
    }

    public <T extends Member> T getMember() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof MemberInfo memberInfo) {

            return (T)memberInfo.getMember();
        }

        return null;
    }
}