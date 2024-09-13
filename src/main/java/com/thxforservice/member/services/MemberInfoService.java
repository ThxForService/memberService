package com.thxforservice.member.services;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.thxforservice.file.entities.FileInfo;
import com.thxforservice.file.services.FileInfoService;
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
    private final FileInfoService fileInfoService;

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

        addInfo(member);

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
            /**
             * sopt 검색옵션
             * ALL - (통합검색) - email, userName
             * email - 이메일로 검색
             * userName - 닉네임으로 검색
             */

            sopt = sopt.trim();
            skey = skey.trim();
            StringExpression expression = null;

            if (sopt.equals("ALL")) { // 통합 검색

                expression = member.email.concat(member.username)
                        .concat(member.mobile);


            } else if (sopt.equals("name")) { // 회원명
                expression = member.username;

            } else if (sopt.equals("email")) { // 이메일
                expression = member.email;
            }

            if (expression != null) andBuilder.and(expression.contains(skey));

        }

        List<String> email = search.getEmail();
        if (email != null && !email.isEmpty()) {
            andBuilder.and(member.email.in(email));
        }
        List<String> authoritiy = search.getAuthority();
        if (authoritiy != null && !authoritiy.isEmpty()) {
            List<Authority> authorities = authoritiy.stream().map(Authority::valueOf).toList();
            andBuilder.and(member.authority.in(authorities)); // 권한 체크
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


    public void addInfo(Member member) {
        /*
        try {
            List<FileInfo> files = fileInfoService.getList(member.getGid());
            if (files != null && !files.isEmpty()) {
                member.setProfileImage(files.get(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

         */
    }

}
