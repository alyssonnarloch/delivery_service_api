package com.api.delivery_service_api.custom_validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = {NotEmptyValidator.class})
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface INotEmpty {

    String message() default "Campo deve ser informado.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
