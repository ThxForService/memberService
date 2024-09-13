package com.thxforservice.global.tests;

import com.thxforservice.member.constants.Status;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)

public @interface MockMember {
    long seq() default 1L;

    String gid() default "testgid";

    String email() default "user01@test.org";

    String password() default "_aA123456";

    String username() default "테스트사용자";

    String mobile() default "01012345678";

    Status status() default Status.UNDERGRADUATE;

    String authority() default "STUDENT";

}
