package com.github.mohrezal.api.shared.annotations.range;

import com.github.mohrezal.api.shared.enums.MessageKey;
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

    String minMessage() default MessageKey.SHARED_VALIDATION_RANGE_MIN;

    String maxMessage() default MessageKey.SHARED_VALIDATION_RANGE_MAX;

    String message() default MessageKey.SHARED_VALIDATION_RANGE;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
