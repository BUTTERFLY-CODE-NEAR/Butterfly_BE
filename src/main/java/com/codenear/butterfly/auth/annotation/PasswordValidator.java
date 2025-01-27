package com.codenear.butterfly.auth.annotation;

import com.codenear.butterfly.auth.domain.dto.AuthRegisterDTO;
import com.codenear.butterfly.member.domain.dto.ResetPasswordRequestDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<ValidPassword, Object> {

    private static final int MIN_PASSWORD_LENGTH = 10;
    private static final int MAX_PASSWORD_LENGTH = 18;

    @Override
    public boolean isValid(Object dto, ConstraintValidatorContext context) {
        String password = null;

        if (dto instanceof AuthRegisterDTO) {
            password = ((AuthRegisterDTO) dto).getPassword();
        } else if (dto instanceof ResetPasswordRequestDTO) {
            password = ((ResetPasswordRequestDTO) dto).getNewPassword();
        }

        if (password == null || password.isBlank()) {
            buildValidatorMessage(context, "비밀번호 입력은 필수입니다.");
            return false;
        }

        if (password.length() < MIN_PASSWORD_LENGTH || password.length() > MAX_PASSWORD_LENGTH) {
            buildValidatorMessage(context, "비밀번호는 10자 ~ 18자 입니다.");
            return false;
        }

        if (password.matches("[a-zA-Z0-9]*")) {
            buildValidatorMessage(context, "특수문자가 1자 이상 필수로 요구 됩니다.");
            return false;
        }

        if (!password.matches("[a-zA-Z0-9!@^&]*")) {
            buildValidatorMessage(context, "특수 문자는 !, @, ^, & 만 허용됩니다.");
            return false;
        }
        return true;
    }

    private void buildValidatorMessage(ConstraintValidatorContext context, String errorMessage) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(errorMessage)
                .addPropertyNode("password")
                .addConstraintViolation();
    }
}