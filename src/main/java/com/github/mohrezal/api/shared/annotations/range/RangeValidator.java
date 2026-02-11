package com.github.mohrezal.api.shared.annotations.range;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RangeValidator implements ConstraintValidator<Range, Integer> {

    private int min;
    private int max;
    private String minMessage;
    private String maxMessage;

    @Override
    public void initialize(Range annotation) {
        this.min = annotation.min();
        this.max = annotation.max();
        this.minMessage = annotation.minMessage();
        this.maxMessage = annotation.maxMessage();
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        context.disableDefaultConstraintViolation();

        if (value < min) {
            context.buildConstraintViolationWithTemplate(
                            minMessage.replace("{min}", String.valueOf(min)))
                    .addConstraintViolation();
            return false;
        }
        if (value > max) {
            context.buildConstraintViolationWithTemplate(
                            maxMessage.replace("{max}", String.valueOf(max)))
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
