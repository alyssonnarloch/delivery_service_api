package com.api.delivery_service_api.custom_validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EmailValidator implements ConstraintValidator<IEmail, String> {

    @Override
    public void initialize(IEmail constraintAnnotation) {

    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        org.apache.commons.validator.EmailValidator validator = org.apache.commons.validator.EmailValidator.getInstance();
        return validator.isValid(email);
    }

}
