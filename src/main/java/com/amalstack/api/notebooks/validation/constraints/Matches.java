package com.amalstack.api.notebooks.validation.constraints;

import com.amalstack.api.notebooks.validation.MatchesValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@SuppressWarnings("unused")
@Constraint(validatedBy = MatchesValidator.class)
@Documented
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Matches.List.class)
public @interface Matches {
    String field();

    String otherField();

    String message() default "{com.amalstack.api.notebooks.validation.constraints.Matches.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Documented
    @Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        Matches[] value();
    }
}
