package com.api.delivery_service_api.custom_validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NotEmptyValidator implements ConstraintValidator<INotEmpty, String> {

    @Override
    public void initialize(INotEmpty a) {
    }

    @Override
    public boolean isValid(String text, ConstraintValidatorContext cvc) {
        System.out.println("OIEEEEEEEEEEEEEEEE");
        return text != null && !text.equals("");
    }

}
