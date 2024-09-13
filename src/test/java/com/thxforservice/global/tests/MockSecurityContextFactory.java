package com.thxforservice.global.tests;

import com.thxforservice.member.constants.Authority;
import com.thxforservice.member.entities.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.stereotype.Component;

import java.security.Security;

@Component
@RequiredArgsConstructor
public class MockSecurityContextFactory implements WithSecurityContextFactory<MockMember> {

    @Autowired
    private PasswordEncoder encoder;

    public SecurityContext createSecurityContext(MockMember mockMember) {
        Member member = new Member();
        member.setEmail(mockMember.email());
        member.setPassword(encoder.encode(mockMember.password()));
        member.setUsername(mockMember.username());
        member.setMobile(mockMember.mobile());
        member.setGid(mockMember.gid());

       return null;
    }

}
