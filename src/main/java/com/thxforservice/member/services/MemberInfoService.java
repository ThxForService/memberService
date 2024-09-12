package com.thxforservice.member.services;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.thxforservice.global.ListData;
import com.thxforservice.global.Pagination;
import com.thxforservice.member.MemberInfo;
import com.thxforservice.member.constants.Authority;
import com.thxforservice.member.controllers.MemberSearch;
import com.thxforservice.member.entities.Member;
import com.thxforservice.member.entities.QMember;
import com.thxforservice.member.entities.QStudent;
import com.thxforservice.member.repositories.EmployeeRepository;
import com.thxforservice.member.repositories.MemberRepository;
import com.thxforservice.member.repositories.StudentRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberInfoService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final EmployeeRepository employeeRepository;
    private final StudentRepository studentRepository;
    private final JPAQueryFactory queryFactory;
    private final HttpServletRequest request;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Member member = memberRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(username));

        Authority authority = member.getAuthority();
        if (authority == Authority.COUNSELOR) {
            member = employeeRepository.findById(member.getMemberSeq()).orElseThrow(() -> new UsernameNotFoundException(username));
        } else if (authority == Authority.STUDENT) {
            member = studentRepository.findById(member.getMemberSeq()).orElseThrow(() -> new UsernameNotFoundException(username));
        }


        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(authority.name()));

        return MemberInfo.builder()
                .email(member.getEmail())
                .password(member.getPassword())
                .member(member)
                .authorities(authorities)
                .build();
    }

    public List<Member> getMembers() {
        return memberRepository.findAll();
    }

    /**
     * 회원 목록 조회
     *
     * @param search
     * @return
     */
    @Transactional
    public ListData<Member> getList(MemberSearch search) {
        int page = Math.max(search.getPage(), 1);
        int limit = search.getLimit();
        limit = limit < 1 ? 20 : limit;
        int offset = (page - 1) * limit;

        /* 검색 처리 S */
        BooleanBuilder andBuilder = new BooleanBuilder();
        QMember member = QMember.member; // 학번은 멤버를 가져올 때 다르게 가져와야 할 듯...


        String sopt = search.getSopt();
        String skey = search.getSkey();
        sopt = StringUtils.hasText(sopt) ? sopt.toUpperCase() : "ALL";
        if (StringUtils.hasText(skey)) {
            skey = skey.trim();
            if (sopt.equals("ALL")) { // 통합 검색

            } else if (sopt.equals("name")) { // 회원명, 지도교수명

            }

        }

        List<String> email = search.getEmail();
        if (email != null && !email.isEmpty()) {
            andBuilder.and(member.email.in(email)); // 학생을 그룹 지어서 조회
        }
        List<String> authoritiy = search.getAuthority();
        if (authoritiy != null && !authoritiy.isEmpty()) {
            List<Authority> authorities = authoritiy.stream().map(Authority::valueOf).toList();
            andBuilder.and(member.authority.in(authorities)); // 권환 체크
        }




        /* 검색 처리 E */

        List<Member> items = queryFactory.selectFrom(member)
                .fetchJoin()
                .where(andBuilder)
                .offset(offset)
                .limit(limit)
                .orderBy(member.createdAt.desc())
                .fetch();

        long total = memberRepository.count(andBuilder);
        Pagination pagination = new Pagination(page, (int)total, 10, limit, request);

        return new ListData<>(items, pagination);
    }
}
