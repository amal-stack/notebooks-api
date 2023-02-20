package com.amalstack.api.notebooks.validation;

import com.amalstack.api.notebooks.NotebooksApiApplication;
import com.amalstack.api.notebooks.dto.AppUserRegistrationDto;
import com.amalstack.api.notebooks.validation.constraints.Matches;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.util.PropertyPlaceholderHelper;

import javax.validation.ConstraintValidatorContext;
import java.io.IOException;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MatchesValidatorTest {
    @Mock
    private Matches matches;
    @Mock
    private ConstraintValidatorContext constraintValidatorContext;
    private MatchesValidator matchesValidator;
    private AutoCloseable closeable;
    private Properties validationMessages;

    @BeforeAll
    void init() throws IOException {
        closeable = MockitoAnnotations.openMocks(this);

        validationMessages = new Properties();
        final String validationMessagesPropertiesFile = "/ValidationMessages.properties";
        validationMessages.load(NotebooksApiApplication.class
                .getResourceAsStream(validationMessagesPropertiesFile));
    }

    @BeforeEach
    void initEach() {
        when(matches.field()).thenReturn("password");
        when(matches.otherField()).thenReturn("confirmPassword");

        matchesValidator = new MatchesValidator();
        matchesValidator.initialize(matches);

    }

    @Test
    void isValid_whenFieldsAreEqual_thenReturnTrue() {

        var registration = new AppUserRegistrationDto("test@example.com",
                "Test User",
                "Abcdefg@123",
                "Abcdefg@123");

        assertThat(
                matchesValidator.isValid(registration, constraintValidatorContext))
                .isTrue();
    }

    @Test
    void isValid_whenFieldsAreNotEqual_thenReturnFalse() {

        var registration = new AppUserRegistrationDto("test@example.com",
                "Test User",
                "Abcdefg@123",
                "Abcdefg@1234");

        assertThat(
                matchesValidator.isValid(registration, constraintValidatorContext))
                .isFalse();
    }

    @Test
    void isValid_whenPropertyNotFound_thenThrowsInvalidPropertyException() {
        Matches invalidPropertyMatches = Mockito.mock(Matches.class);
        when(invalidPropertyMatches.field()).thenReturn("nonExistingProperty");
        when(invalidPropertyMatches.otherField()).thenReturn("nonExistingOtherProperty");

        var registration = new AppUserRegistrationDto("test@example.com",
                "Test User",
                "Abcdefg@123",
                "Abcdefg@123");
        matchesValidator.initialize(invalidPropertyMatches);

        assertThatExceptionOfType(InvalidPropertyException.class)
                .isThrownBy(() -> matchesValidator.isValid(registration, constraintValidatorContext));
    }

    @Test
    @Disabled
    void isValid_whenInvalid_throwsWithDefaultErrorMessage() {
        when(matches.message()).thenReturn("{com.amalstack.api.notebooks.validation.constraints.Matches.message}");

        var placeholderHelper = new PropertyPlaceholderHelper("{", "}");
        String message = placeholderHelper.replacePlaceholders(
                matches.message(), validationMessages);

        message = placeholderHelper.replacePlaceholders(message,
                placeholderName -> switch (placeholderName) {
                    case "field" -> matches.field();
                    case "otherField" -> matches.otherField();
                    default -> null;
                });

    }

    @AfterAll
    void releaseMocks() throws Exception {
        closeable.close();
    }
}