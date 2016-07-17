package com.api.delivery_service_api.custom_validation;

import java.util.List;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ListNotEmptyValidator implements ConstraintValidator<IListNotEmpty, List> {

    @Override
    public void initialize(IListNotEmpty constraintAnnotation) {

    }

    @Override
    public boolean isValid(List list, ConstraintValidatorContext context) {
        return list.size() > 0;
    }

}
