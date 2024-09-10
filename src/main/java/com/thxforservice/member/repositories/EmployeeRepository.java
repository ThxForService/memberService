package com.thxforservice.member.repositories;

import com.thxforservice.member.entities.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface EmployeeRepository extends JpaRepository<Employee, Long>, QuerydslPredicateExecutor<Employee> {

}
