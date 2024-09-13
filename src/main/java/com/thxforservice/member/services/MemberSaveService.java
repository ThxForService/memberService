package com.thxforservice.member.services;

import com.thxforservice.global.Utils;
import com.thxforservice.member.MemberUtil;
import com.thxforservice.member.constants.Authority;
import com.thxforservice.member.constants.Status;
import com.thxforservice.member.controllers.RequestJoin;
import com.thxforservice.member.entities.Employee;
import com.thxforservice.member.entities.Member;
import com.thxforservice.member.entities.Student;
import com.thxforservice.member.repositories.EmployeeRepository;
import com.thxforservice.member.repositories.MemberRepository;
import com.thxforservice.member.repositories.StudentRepository;
import com.thxforservice.mypage.controllers.RequestProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.thxforservice.member.exceptions.MemberNotFoundException;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberSaveService {
    private final MemberRepository memberRepository;
    private final EmployeeRepository employeeRepository;
    private final StudentRepository studentRepository;
    private final MemberInfoService infoService;

    private final PasswordEncoder passwordEncoder;
    private final MemberUtil memberUtil;
    private final Utils utils;


    /**
     * 회원 가입 처리
     *
     * @param form
     */
    public void save(RequestJoin form) {
        Authority authority = StringUtils.hasText(form.getAuthority()) ? Authority.valueOf(form.getAuthority()) : Authority.STUDENT;

        Member member = null;
        if (authority == Authority.COUNSELOR) { // 상담원
            member = new Employee();
        } else { // 학생
            member = new Student();
        }

        /* 공통 항목 처리 S */
        String hash = passwordEncoder.encode(form.getPassword()); // BCrypt 해시화
        String mobile = form.getMobile();
        if (StringUtils.hasText(mobile)) {
            mobile = mobile.replaceAll("\\D", "");
        }
        member.setEmail(form.getEmail());
        member.setUsername(form.getUsername());
        member.setPassword(hash);
        member.setMobile(mobile);
        member.setBirthdate(form.getBirthDate());
        member.setAuthority(authority);
        member.setZonecode(form.getZonecode());
        member.setAddress(form.getAddress());
        member.setAddressSub(form.getAddressSub());
        member.setGid(form.getGid());
        /* 공통 항목 처리 E */

        // 상담사 추가 정보
        if (member instanceof Employee employee) {
            employee.setEmpNo(form.getEmpNo());
            employee.setIntroduction(form.getIntroduction());
            employee.setSubject(form.getSubject());
            employee.setRating(form.getRating());
            employee.setStatus(form.getStatus() == null ? Status.EMPLOYED : Status.valueOf(form.getStatus()));

            employeeRepository.saveAndFlush(employee);

        } else if (member instanceof Student student){ // 학생 추가 정보
            student.setStudentNo(form.getStudentNo());
            student.setGrade(form.getGrade());
            student.setDepartment(form.getDepartment());
            student.setStatus(form.getStatus() == null ? Status.UNDERGRADUATE : Status.valueOf(form.getStatus()));

            studentRepository.saveAndFlush(student);
        }
    }

    /**
     * 회원정보 수정
     * @param form
     */
    public Member save(RequestProfile form) {
        Member member = memberUtil.getMember();
        String email = member.getEmail();
        member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);

        Authority authority = member.getAuthority();

        if (authority == authority.STUDENT) {
            member = studentRepository.findById(member.getMemberSeq()).orElseThrow(MemberNotFoundException::new);
        } else {
            member = employeeRepository.findById(member.getMemberSeq()).orElseThrow(MemberNotFoundException::new);
        }

        /* 공통 수정 항목 B */
        String password = form.getPassword();
        if (StringUtils.hasText(password)) {
            String hash = passwordEncoder.encode(password);
            member.setPassword(hash);
        }

        String mobile = form.getMobile();
        if (StringUtils.hasText(mobile)) {
            mobile = mobile.replaceAll("\\D", "");
        }

        member.setMobile(mobile);
        member.setUsername(form.getUsername());
        member.setZonecode(form.getZonecode());
        member.setAddress(form.getAddress());
        member.setAddressSub(form.getAddressSub());
        member.setBirthdate(form.getBirthdate());

        /* 공통 수정 항목 D */

        // 교직원(상담사, 교수), 관리자
        if (member instanceof Employee employee) {
            employee.setEmpNo(form.getEmpNo());
            employee.setStatus(Status.valueOf(form.getStatus()));
            employee.setSubject(form.getSubject());
            employeeRepository.saveAndFlush(employee);
        } else if (member instanceof Student student) {
            Employee employee = employeeRepository.findById(form.getProfessorSeq()).orElse(null);
            student.setStudentNo(form.getStudentNo());
            student.setStatus(Status.valueOf(form.getStatus()));
            student.setDepartment(form.getDepartment());
            student.setGrade(form.getGrade());
            student.setProfessor(employee);
            studentRepository.saveAndFlush(student);
        }

        return member;
    }


}
