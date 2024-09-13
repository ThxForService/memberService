package com.thxforservice.mypage.validators;

import com.thxforservice.global.validators.MobileValidator;
import com.thxforservice.global.validators.PasswordValidator;
import com.thxforservice.member.constants.Authority;
import com.thxforservice.mypage.controllers.RequestProfile;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class ProfileUpdateValidator implements Validator, PasswordValidator, MobileValidator {

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(RequestProfile.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (errors.hasErrors()) { // 커맨드 객체 검증 실패시에는 종료
            return;
        }

        /**
         *  1. 비밀번호가 입력된 경우
         *      -자리수 체크
         *      -비밀번호 복잡성 체크
         *      -비밀번호 복잡성 체크
         *      -비밀번호 확인 일치 여부 체크
         *  2. 휴대전화번호가 입력된 경우
         *      -형식체크
         */

        RequestProfile form = (RequestProfile) target;
        String email = form.getEmail();
        String password = form.getPassword();
        String confirmPassword = form.getConfirmPassword();
        String mobile = form.getMobile();

        // 관리자에서 회원 정보 수정시 이메일 정보 필수
        if (!StringUtils.hasText(email)) {
            errors.rejectValue("email", "NotBlank");
        }

        // 1. 비밀번호가 입력된 경우
        if (StringUtils.hasText(password)) {
            if (password.length() < 8) { // 자리수 체크
                errors.rejectValue("password", "Size");
            }

            if (!password.equals(confirmPassword)) { // 비밀번호 일치성 체크
                errors.rejectValue("confirmPassword", "Mismatch.password");
            }

            if (!alphaCheck(password, false) || !numberCheck(password) || !specialCharsCheck(password)) {
                errors.rejectValue("password", "Complexity"); // 비밀번호 복잡성 체크 (알파벳,숫자,특수문자)
            }
        }

        // 2. 휴대전화번호가 입력된 경우
        if (StringUtils.hasText(mobile) && !mobileCheck(mobile)) {
            errors.rejectValue("mobile", "Mobile");
        }

        Authority authority = Authority.valueOf(form.getAuthority());
        // 추가 필수 항목 체크
        if (authority == Authority.STUDENT) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "studentNo", "NotBlank");
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "department", "NotBlank");
        } else {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "empNo", "NotBlank");
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "subject", "NotBlank");
        }

    }
}