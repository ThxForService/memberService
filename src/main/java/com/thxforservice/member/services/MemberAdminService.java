package com.thxforservice.member.services;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.thxforservice.global.ListData;
import com.thxforservice.global.Pagination;
import com.thxforservice.member.controllers.MemberSearch;
import com.thxforservice.member.entities.Member;
import com.thxforservice.member.entities.QMember;
import com.thxforservice.member.repositories.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.querydsl.core.BooleanBuilder;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberAdminService {
    private final MemberRepository memberRepository;
    private final JPAQueryFactory queryFactory;
    private final HttpServletRequest request;

    public Member deleteMember(Long memberSeq) {
        Member member = memberRepository.findById(memberSeq).orElse(null);
        if (member != null) {

            member.setDeletedAt(LocalDateTime.now());

            memberRepository.delete(member);
            //memberRepository.saveAndFlush(member);
            return member;
        }
        return null;
    }

    public ListData<Member> getList(MemberSearch search) {

        int page = Math.max(search.getPage(), 1);
        int limit = search.getLimit();
        limit = limit > 0 ? limit : 20;

        int offset = (page - 1) * limit;

        String sopt = search.getSopt();
        String skey = search.getSkey();

        QMember member = QMember.member;
        BooleanBuilder andBuilder = new BooleanBuilder();

        /**
         * 조건 검색 처리
         *
         * sopt - ALL : 통합검색(이름 + 이메일 )
         *       NAME : 이름
         *       EMAIL : 이메일
         */
        sopt = sopt != null && StringUtils.hasText(sopt.trim()) ? sopt.trim() : "ALL";
        if (skey != null && StringUtils.hasText(skey.trim())) {
            skey = skey.trim();
            BooleanExpression condition = null;

            BooleanBuilder orBuilder = new BooleanBuilder();

            BooleanBuilder nameCondition = new BooleanBuilder();

            if (sopt.equals("ALL")) { // 통합 검색
                orBuilder.or(member.username.concat(member.email)
                                .contains(skey))
                        .or(nameCondition);

            } else if (sopt.equals("NAME")) { // 회원명 검색
                condition = member.username.contains(skey);
            } else if (sopt.equals("EMAIL")) { // 이메일 검색
                condition = member.email.contains(skey);
            }

            if (condition != null) andBuilder.and(condition);
            andBuilder.and(orBuilder);
        }

        String sort = search.getSort();

        PathBuilder<Member> pathBuilder = new PathBuilder<>(Member.class, "member");
        OrderSpecifier orderSpecifier = null;
        Order order = Order.DESC;
        if (sort != null && StringUtils.hasText(sort.trim())) {
            // 정렬항목_방향   예) viewCount_DESC -> 조회수가 많은 순으로 정렬
            String[] _sort = sort.split("_");
            if (_sort[1].toUpperCase().equals("ASC")) {
                order = Order.ASC;
            }

            orderSpecifier = new OrderSpecifier(order, pathBuilder.get(_sort[0]));
        }

        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();

        if (orderSpecifier != null) {
            orderSpecifiers.add(orderSpecifier);
        }

        List<Member> items = queryFactory
                .selectFrom(member)
                .where(andBuilder)
                //.orderBy(orderSpecifiers.toArray(OrderSpecifier[]::new))
                //.orderBy(orderSpecifier, member.createdAt.desc())
                .orderBy(member.createdAt.desc())
                .offset(offset)
                .limit(limit)
                .fetch();


        // 전체 갯수
        long total = memberRepository.count(andBuilder);

        // 페이징 처리
        int ranges = 10;

        Pagination pagination = new Pagination(page, (int)total, ranges, limit, request);

        return new ListData<>(items, pagination);

    }
}
