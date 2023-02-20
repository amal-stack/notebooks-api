package com.amalstack.api.notebooks.dto;

import com.amalstack.api.notebooks.model.AppUser;
import com.amalstack.api.notebooks.validation.constraints.Matches;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Matches(
        field = "password",
        otherField = "confirmPassword",
        message = "Password and Confirm Password values do not match")
public record AppUserRegistrationDto(
        @Email
        String email,
        @NotBlank
        String name,
        @NotBlank
        String password,
        @NotBlank
        String confirmPassword) implements Serializable {

    public AppUser toUser(PasswordEncoder encoder) {
        return new AppUser(email, name, encoder.encode(password));
    }
}
