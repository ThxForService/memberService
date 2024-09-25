package com.thxforservice.member.repositories;

import com.thxforservice.member.entities.QStudent;
import com.thxforservice.member.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface StudentRepository extends JpaRepository<Student, Long>, QuerydslPredicateExecutor<Student> {
    default boolean exists(Long studentNo) {
        QStudent student = QStudent.student;
        return exists(student.studentNo.eq(studentNo));
    }
}
