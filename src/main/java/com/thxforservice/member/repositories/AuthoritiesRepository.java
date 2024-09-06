package org.thxforservice.member.repositories;

import org.thxforservice.member.entities.Authorities;
import org.thxforservice.member.entities.AuthoritiesId;
import org.thxforservice.member.entities.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface AuthoritiesRepository extends JpaRepository<Authorities, AuthoritiesId>, QuerydslPredicateExecutor<Authorities> {

    List<Authorities> findByMember(Member member);
}