package com.amalstack.api.notebooks.repository;

import com.amalstack.api.notebooks.model.AppUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AppUserRepositoryTest {

    @Autowired
    private AppUserRepository appUserRepository;


    @Test
    void injectedComponentsAreNotNull() {
        assertThat(appUserRepository).isNotNull();
    }

    @Test
    void findByUsername_whenSaved_thenFindsByUsername() {
        AppUser appUser = new AppUser(
                "test1@example.com",
                "Test User",
                "password");

        var savedAppUser = appUserRepository.save(appUser);

        var foundAppUser = appUserRepository.findByUsername("test1@example.com");

        assertThat(foundAppUser).isPresent();
        assertThat(foundAppUser.get().getUsername()).isEqualTo(appUser.getUsername());
        assertThat(foundAppUser.get().getName()).isEqualTo(appUser.getName());
        assertThat(foundAppUser.get().getPassword()).isEqualTo(appUser.getPassword());
        assertThat(foundAppUser.get().getId()).isEqualTo(savedAppUser.getId());

    }

    @Test
    void findByUsername_whenUserDoesNotExist_thenReturnedOptionalIsEmpty() {
        var foundAppUser = appUserRepository.findByUsername("notfound@example.com");
        assertThat(foundAppUser).isNotPresent();
    }
}
