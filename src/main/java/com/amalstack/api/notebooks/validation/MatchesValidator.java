package com.amalstack.api.notebooks.validation;

import com.amalstack.api.notebooks.validation.constraints.Matches;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

public class MatchesValidator implements ConstraintValidator<Matches, Object> {
    private String fieldName;
    private String otherFieldName;

    @Override
    public void initialize(Matches matchesAnnotation) {
        fieldName = matchesAnnotation.field();
        otherFieldName = matchesAnnotation.otherField();
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext constraintValidatorContext) {
        var beanWrapper = new BeanWrapperImpl(object);
        var field = beanWrapper.getPropertyValue(fieldName);
        var otherField = beanWrapper.getPropertyValue(otherFieldName);
        if (field == null) {
            return otherField == null;
        }
        return field.equals(otherField);
    }
}
