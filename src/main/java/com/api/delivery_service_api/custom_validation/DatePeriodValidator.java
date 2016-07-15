package com.api.delivery_service_api.custom_validation;

import com.api.delivery_service_api.modelaux.Period;
import java.util.Date;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DatePeriodValidator implements ConstraintValidator<IDatePeriod, Period> {

    @Override
    public void initialize(IDatePeriod a) {
        
    }

    @Override
    public boolean isValid(Period p, ConstraintValidatorContext cvc) {
        Date today = new Date();

        return p.getStartAt() != null && p.getEndAt() != null && p.getStartAt().getTime() >= today.getTime() && p.getEndAt().getTime() > p.getStartAt().getTime();
    }

}
