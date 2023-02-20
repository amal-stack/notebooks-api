package com.amalstack.api.notebooks.validation;

import com.amalstack.api.notebooks.exception.ResourceNotOwnedException;
import com.amalstack.api.notebooks.model.AppUser;
import com.amalstack.api.notebooks.model.Notebook;
import com.amalstack.api.notebooks.model.Page;
import com.amalstack.api.notebooks.model.Section;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class OwnershipGuardTest {

    private static User user1;
    private static User user2;
    private static Notebook notebook;
    private static Section section;
    private static Page page;


    @BeforeAll
    static void initAll() {
        user1 = new User("test1@example.com", "password1", Collections.emptyList());
        user2 = new User("test2@example.com", "password1", Collections.emptyList());

        notebook = new Notebook(1,
                "Test Notebook",
                "Test Description",
                LocalDateTime.now(),
                new AppUser(1L, "test1@example.com", "Test User", "password"));

        section = new Section(1L, "Test Section", notebook);

        page = new Page(1L, "Test Page", "Test Page Contents", section);
    }

    @Test
    void throwIfNotebookNotOwned_whenOwned_thenDoesNotThrow() {
        assertDoesNotThrow(() -> OwnershipGuard.throwIfNotebookNotOwned(user1, notebook));
    }

    @Test
    void throwIfPageNotOwned_whenOwned_thenDoesNotThrow() {
        assertDoesNotThrow(() -> OwnershipGuard.throwIfPageNotOwned(user1, page));
    }

    @Test
    void throwIfSectionNotOwned_whenOwned_thenDoesNotThrow() {
        assertDoesNotThrow(() -> OwnershipGuard.throwIfSectionNotOwned(user1, section));
    }

    @Test
    void throwIfNotebookNotOwned_whenNotOwned_thenThrows() {
        assertThatExceptionOfType(ResourceNotOwnedException.class)
                .isThrownBy(() -> OwnershipGuard.throwIfNotebookNotOwned(user2, notebook));
    }

    @Test
    void throwIfPageNotOwned_whenNotOwned_thenThrows() {
        assertThatExceptionOfType(ResourceNotOwnedException.class)
                .isThrownBy(() -> OwnershipGuard.throwIfPageNotOwned(user2, page));
    }

    @Test
    void throwIfSectionNotOwned_whenNotOwned_thenThrows() {
        assertThatExceptionOfType(ResourceNotOwnedException.class)
                .isThrownBy(() -> OwnershipGuard.throwIfSectionNotOwned(user2, section));
    }
}