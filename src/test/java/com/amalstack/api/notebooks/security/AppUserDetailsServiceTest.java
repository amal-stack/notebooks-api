package com.amalstack.api.notebooks.security;

import com.amalstack.api.notebooks.model.AppUser;
import com.amalstack.api.notebooks.repository.AppUserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

//@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AppUserDetailsServiceTest {

    private static final String EXISTING_USERNAME = "test1@example.com";
    private static final String NON_EXISTING_USERNAME = "notfound@example.com";

    @Mock
    private AppUserRepository appUserRepository;

    @InjectMocks
    private AppUserDetailsService appUserDetailsService;

    private AutoCloseable closeable;


    @BeforeAll
    void initAll() {
        closeable = MockitoAnnotations.openMocks(this);
        AppUser appUser1 = new AppUser(1L,
                EXISTING_USERNAME,
                "Test User",
                "password");

        when(appUserRepository.findByUsername(EXISTING_USERNAME))
                .thenReturn(Optional.of(appUser1));
        when(appUserRepository.findByUsername(NON_EXISTING_USERNAME))
                .thenReturn(Optional.empty());
    }


    @Test
    void loadUserByUsername_whenUsernameDoesNotExist_thenThrowUsernameNotFoundException() {
        assertThatExceptionOfType(UsernameNotFoundException.class)
                .isThrownBy(() -> appUserDetailsService.loadUserByUsername(NON_EXISTING_USERNAME))
                .withMessage("The username " + NON_EXISTING_USERNAME + " was not found");
    }

    @Test
    void loadUserByUsername_whenUsernameExists_returnsUserDetails() {
        UserDetails userDetails = appUserDetailsService.loadUserByUsername(EXISTING_USERNAME);
        assertThat(userDetails.getUsername()).isEqualTo(EXISTING_USERNAME);
        assertThat(userDetails.getPassword()).isEqualTo("password");
        assertThat(userDetails.getAuthorities()).isEmpty();
    }

    @AfterAll
    void releaseMocks() throws Exception {
        closeable.close();
    }
}