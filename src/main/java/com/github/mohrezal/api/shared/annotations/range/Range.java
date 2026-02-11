package com.github.mohrezal.api.shared.annotations.range;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = RangeValidator.class)
public @interface Range {
    int min() default 0;

    int max() default Integer.MAX_VALUE;

    String minMessage() default "Value must be at least {min}";

    String maxMessage() default "Value cannot exceed {max}";

    String message() default "Value must be between {min} and {max}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
