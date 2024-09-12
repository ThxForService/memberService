package com.thxforservice.member.services;

import com.thxforservice.member.MemberUtil;
import com.thxforservice.member.constants.Authority;
import com.thxforservice.member.constants.Status;
import com.thxforservice.member.controllers.RequestJoin;
import com.thxforservice.member.controllers.RequestUpdate;
import com.thxforservice.member.entities.Employee;
import com.thxforservice.member.entities.Member;
import com.thxforservice.member.entities.Student;
import com.thxforservice.member.repositories.EmployeeRepository;
import com.thxforservice.member.repositories.MemberRepository;
import com.thxforservice.member.repositories.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
            employee.setStatus(Status.valueOf(form.getStatus()));

            employeeRepository.saveAndFlush(employee);

        } else if (member instanceof Student student){ // 학생 추가 정보
            student.setStudentNo(form.getStudentNo());
            student.setGrade(form.getGrade());
            student.setDepartment(form.getDepartment());
            student.setStatus(Status.valueOf(form.getStatus()));

            studentRepository.saveAndFlush(student);
        }
    }

    /**
     * 회원정보 수정
     * @param form
     */
    public void save(RequestUpdate form) {

    }


}
