package com.thxforservice.member.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thxforservice.member.constants.Authority;
import com.thxforservice.member.constants.Status;
import com.thxforservice.member.controllers.RequestJoin;
import com.thxforservice.member.entities.Employee;
import com.thxforservice.member.entities.Student;
import com.thxforservice.member.repositories.EmployeeRepository;
import com.thxforservice.member.repositories.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class MemberSaveServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MemberSaveService saveService;

    private RequestJoin form;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    private List<RequestJoin> createDummyUsers() {
        return IntStream.rangeClosed(1, 10) // 1부터 10까지 범위 생성
                .mapToObj(i -> createRequestJoin(
                        String.format("user%d@test.org", i),                // 이메일: user1@test.org ~ user10@test.org
                        String.format("사용자%d", i),                        // 사용자 이름: 사용자1 ~ 사용자10
                        "_aA123456",                                        // 동일한 비밀번호
                        String.format("010-1000-10%02d", i),                // 휴대전화: 010-1000-1001 ~ 010-1000-1010
                        LocalDate.of(1990 + i, i, i)))                      // 생년월일: 1991-01-01 ~ 2000-10-10
                .collect(Collectors.toList());
    }

    // RequestJoin 객체를 만드는 유틸 메서드
    private RequestJoin createRequestJoin(String email, String username, String password, String mobile, LocalDate birthDate) {
        RequestJoin form = new RequestJoin();
        form.setEmail(email);
        form.setPassword(password);
        form.setConfirmPassword(password);
        form.setMobile(mobile);
        form.setUsername(username);
        form.setBirthDate(birthDate);
        form.setStatus(String.valueOf(Status.EMPLOYED));
        form.setEmpNo(12345L + (long) (Math.random() * 10000)); // 고유한 EmpNo 생성
        form.setAuthority(String.valueOf(Authority.COUNSELOR));
        form.setAgree(true);
        return form;
    }

    // 테스트 실행 전에 10명의 더미 데이터를 미리 생성
    @BeforeEach
    @Transactional
    public void init() throws Exception {
        List<RequestJoin> users = createDummyUsers();

        for (RequestJoin user : users) {
            // MockMvc를 사용해 POST 요청으로 회원 가입 API 호출
            String params = om.writeValueAsString(user);
            mockMvc.perform(post("/account")
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(Charset.forName("UTF-8"))
                            .content(params))
                    .andDo(print());
        }
    }

    @Test
    @DisplayName("회원 가입 테스트")
    void joinTest() {
        // 실제 테스트는 회원이 이미 추가된 상태로 진행됩니다.
        // 회원 정보가 추가되었는지 확인하는 테스트 로직을 구현할 수 있습니다.
    }

    @Test
    @DisplayName("회원 가입 테스트")
    void joinTest2() throws Exception {
        RequestJoin form = new RequestJoin();
        form.setEmail("user100@test.org");
        form.setPassword("_aA123456");
        form.setConfirmPassword(form.getPassword());
        form.setMobile("010-1000-1000");
        form.setUsername("사용자100");
        form.setBirthDate(LocalDate.of(1999, 12, 31));
        form.setStatus(String.valueOf(Status.EMPLOYED));
        form.setEmpNo(Long.valueOf("12345"));
        form.setAuthority(String.valueOf(Authority.COUNSELOR));
        form.setAgree(true);

        String params = om.writeValueAsString(form);

        mockMvc.perform(post("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(Charset.forName("UTF-8"))
                        .content(params))
                .andDo(print());
    }

}
