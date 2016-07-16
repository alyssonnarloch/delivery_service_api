package com.api.delivery_service_api.custom_validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = {UserNotExistsValidator.class})
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface IUserNotExists {

    String message() default "Usuário inválido.";

    String name() default "user";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
