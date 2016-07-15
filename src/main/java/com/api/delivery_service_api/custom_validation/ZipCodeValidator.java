package com.api.delivery_service_api.custom_validation;

import com.api.delivery_service_api.extras.Extra;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ZipCodeValidator implements ConstraintValidator<IZipCode, String> {

    @Override
    public void initialize(IZipCode a) {

    }

    @Override
    public boolean isValid(String t, ConstraintValidatorContext cvc) {
        return Extra.zipCodeValid(t);
    }

}
