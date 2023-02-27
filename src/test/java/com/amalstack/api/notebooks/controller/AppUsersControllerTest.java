package com.amalstack.api.notebooks.controller;

import com.amalstack.api.notebooks.dto.AppUserInfoDto;
import com.amalstack.api.notebooks.dto.AppUserRegistrationDto;
import com.amalstack.api.notebooks.exception.AppUserNotFoundException;
import com.amalstack.api.notebooks.exception.UsernameAlreadyExistsException;
import com.amalstack.api.notebooks.model.AppUser;
import com.amalstack.api.notebooks.repository.AppUserRepository;
import com.amalstack.api.notebooks.security.ApplicationSecurityConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(AppUsersController.class)
@Import(ApplicationSecurityConfiguration.class)
@AutoConfigureMockMvc
public class AppUsersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AppUserRepository appUserRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;


    @BeforeEach
    void init() {

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void register_whenUsernameAlreadyExists_thenBadRequest() throws Exception {
        final String existingUsername = "existing@example.com";
        Mockito
                .when(appUserRepository.findByUsername(existingUsername))
                .thenReturn(Optional.of(new AppUser(1L,
                        existingUsername,
                        "Existing User",
                        "password")));

        AppUserRegistrationDto registration = new AppUserRegistrationDto(
                existingUsername,
                "Test User",
                "password",
                "password");

        mockMvc
                .perform(post("/users")
                        //.with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registration))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                //.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().reason("Username already exists"))
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(UsernameAlreadyExistsException.class)
                        .hasMessage("The user with username %s already exists", existingUsername)
                );
    }

    @Test
    void register_whenInputIsInvalid_thenBadRequest() throws Exception {
        final String existingUsername = "invalid-email-address";
        AppUserRegistrationDto registration = new AppUserRegistrationDto(
                existingUsername,
                "",
                "password",
                "password-non-matching");

        mockMvc
                .perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registration))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(jsonPath("$.message")
                                .value("Validation failed"),
                        jsonPath("$.errors.name")
                                .value("must not be blank"),
                        jsonPath("$.errors.email")
                                .value("must be a well-formed email address"),
                        jsonPath("$.errors.appUserRegistrationDto")
                                .value("Password and Confirm Password values do not match"));
    }

    @Test
    void register_whenInputIsValid_thenOk() throws Exception {
        AppUserRegistrationDto registration = new AppUserRegistrationDto("validregistration@example.com",
                "Test User",
                "password",
                "password");

        var appUserInfo = new AppUserInfoDto(null,
                "validregistration@example.com",
                "Test User");

        Mockito
                .when(appUserRepository.save(Mockito.any(AppUser.class)))
                .then(AdditionalAnswers.returnsFirstArg());

        mockMvc
                .perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registration))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(objectMapper.writeValueAsString(appUserInfo)));


    }

    @Test
    @WithMockUser(username = "current_test@example.com")
    void current_whenUserExists_thenOk() throws Exception {
        final String username = "current_test@example.com";
        AppUser appUser = new AppUser(1L,
                username,
                "Test User",
                "password");
        Mockito
                .when(appUserRepository.findByUsername(username))
                .thenReturn(Optional.of(appUser));

        AppUserInfoDto expected = new AppUserInfoDto(1L, username, "Test User");

        mockMvc
                .perform(get("/users"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(objectMapper.writeValueAsString(expected)));
    }

    @Test
    @WithMockUser(username = "current_test_nonexisting@example.com")
    void current_whenUserDoesNotExist_thenBadRequest() throws Exception {
        final String username = "current_test_nonexisting@example.com";
        Mockito
                .when(appUserRepository.findByUsername(username))
                .thenReturn(Optional.empty());

        mockMvc
                .perform(get("/users"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound())
                //.andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().reason("User not found"))
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(AppUserNotFoundException.class)
                        .hasMessage("The user was not found")
                );
    }
}