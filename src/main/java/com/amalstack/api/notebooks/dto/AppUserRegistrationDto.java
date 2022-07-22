package com.amalstack.api.notebooks.dto;

import com.amalstack.api.notebooks.model.AppUser;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.ValidationException;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

public record AppUserRegistrationDto(
        @Email
        String email,
        @NotBlank
        String name,
        @NotBlank
        String password,
        @NotBlank
        String confirmPassword) implements Serializable {

        public boolean passwordMatches() {
                return password.equals(confirmPassword);
        }

        public AppUser toUser(PasswordEncoder encoder) {
                if (!passwordMatches()) {
                        throw new ValidationException("The password and confirm password fields do not match");
                }
                return new AppUser(email, name, encoder.encode(password));
        }
}
