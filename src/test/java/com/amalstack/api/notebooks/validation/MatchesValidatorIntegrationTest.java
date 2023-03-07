package com.amalstack.api.notebooks.validation;

import com.amalstack.api.notebooks.NotebooksApiApplication;
import com.amalstack.api.notebooks.dto.AppUserRegistrationDto;
import com.amalstack.api.notebooks.validation.constraints.Matches;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.util.PropertyPlaceholderHelper;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class MatchesValidatorIntegrationTest {

    private AutoCloseable closeable;

    private Validator validator;

    private Properties validationMessages;

    @BeforeEach
    void initEach() throws IOException {
        closeable = MockitoAnnotations.openMocks(this);

        validator = Validation.buildDefaultValidatorFactory().getValidator();

        validationMessages = new Properties();
        final String validationMessagesPropertiesFile = "/ValidationMessages.properties";
        validationMessages.load(NotebooksApiApplication.class
                .getResourceAsStream(validationMessagesPropertiesFile));
    }

    @Test
    void isValid_whenFieldsAreEqual_thenValid() {

        var registration = new AppUserRegistrationDto("test@example.com",
                "Test User",
                "Abcdefg@123",
                "Abcdefg@123");

        assertThat(validator.validate(registration)).isEmpty();
    }

    @Test
    void isValid_whenBothFieldsAreNull_thenReturnTrue() {
        @Matches(field = "field1", otherField = "field2")
        record Entity(String field1, String field2) {
        }
        var entity = new Entity(null, null);

        assertThat(validator.validate(entity)).isEmpty();
    }

    @Test
    void isValid_whenInvalid_thenProducesConstraintViolationWithDefaultMessage() {

        @Matches(field = "field1",
                otherField = "field2")
        record Entity(String field1, String field2) {
        }

        var entity = new Entity("a-value", "another-value");

        Set<ConstraintViolation<Entity>> violationSet = validator.validate(entity);

        assertThat(violationSet).hasSize(1);
        assertThat(violationSet.iterator().next().getMessage())
                .isEqualTo(interpolate(
                        "{com.amalstack.api.notebooks.validation.constraints.Matches.message}",
                        "field1",
                        "field2"));
    }

    @Test
    void isValid_whenFieldsAreNotEqual_thenProducesConstraintViolationWithCustomMessage() {

        var registration = new AppUserRegistrationDto("test@example.com",
                "Test User",
                "Abcdefg@123",
                "Abcdefg@1234");

        Set<ConstraintViolation<AppUserRegistrationDto>> violationSet = validator.validate(registration);

        assertThat(violationSet).hasSize(1);
        assertThat(violationSet.iterator().next().getMessage())
                .isEqualTo("Password and Confirm Password values do not match");
    }

    @Test
    void isValid_whenInvalid_thenProducesConstraintViolationWithCustomMessageWithInterpolation() {
        @Matches(field = "value1",
                otherField = "value2",
                message = "{field} must have the same value as {otherField}")
        record Entity(String value1, String value2) {
        }

        var entity = new Entity("a-value", "another-value");

        Set<ConstraintViolation<Entity>> violationSet = validator.validate(entity);

        assertThat(violationSet).hasSize(1);
        assertThat(violationSet.iterator().next().getMessage())
                .isEqualTo(interpolate(
                        "{field} must have the same value as {otherField}",
                        "value1",
                        "value2"));
    }


    private String interpolate(String message, String field, String otherField) {
        var placeholderHelper = new PropertyPlaceholderHelper("{", "}");
        return placeholderHelper.replacePlaceholders(message, placeholderName -> switch (placeholderName) {

            case "field" -> field;
            case "otherField" -> otherField;
            default -> validationMessages.getProperty(placeholderName);
        });
    }

    @AfterEach
    void releaseMocks() throws Exception {
        closeable.close();
    }
}
