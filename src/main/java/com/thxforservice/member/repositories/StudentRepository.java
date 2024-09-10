package com.thxforservice.member.repositories;

import com.thxforservice.member.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface StudentRepository extends JpaRepository<Student, Long>, QuerydslPredicateExecutor<Student> {
}
