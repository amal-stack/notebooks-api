package com.amalstack.api.notebooks.repository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class NotebookRepositoryTest {

    @Autowired
    private NotebookRepository notebookRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private PageRepository pageRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    private TestData data;


    @BeforeAll
    void setUp() {
        data = new TestData("NotebookRepositoryTest");
        data.saveToRepositories(appUserRepository, notebookRepository, sectionRepository, pageRepository);
    }

    @Test
    void findByOwnerId_whenUserOwnsNotebooks_thenFindsByOwnerId() {
        var appUserWithNotebooks = data.getAppUserWithNotebooks();
        var notebooks = notebookRepository.findByOwnerId(appUserWithNotebooks.getId());

        assertThat(notebooks).hasSize(data.getNotebooks().size());

        assertThat(notebooks)
                .extracting(notebook -> notebook.getOwner().getId())
                .allMatch(i -> i.equals(appUserWithNotebooks.getId()));
    }

    @Test
    void findByOwnerId_whenUserOwnsNoNotebooks_thenReturnsEmptyCollection() {
        var notebooks = notebookRepository.findByOwnerId(data
                .getAppUserWithoutNotebooks()
                .getId());

        assertThat(notebooks).isEmpty();
    }

    @Test
    void findByOwnerId_whenUserDoesNotExist_thenReturnsEmptyCollection() {
        var notebooks = notebookRepository.findByOwnerId(data
                .nonPersistent()
                .appUser()
                .getId());

        assertThat(notebooks).isEmpty();
    }


    @Test
    void findByOwnerUsername_whenUserOwnsNotebooks_thenFindsByOwnerUsername() {
        var appUserWithNotebooks = data.getAppUserWithNotebooks();
        var notebooks = notebookRepository.findByOwnerUsername(
                appUserWithNotebooks.getUsername());

        assertThat(notebooks).hasSize(2);

        assertThat(notebooks)
                .extracting(notebook -> notebook.getOwner().getUsername())
                .allMatch(u -> u.equals(appUserWithNotebooks.getUsername()));
    }

    @Test
    void findByOwnerUsername_whenUserOwnsNoNotebooks_thenReturnsEmptyCollection() {
        var notebooks = notebookRepository.findByOwnerUsername(
                data.getAppUserWithoutNotebooks().getUsername());

        assertThat(notebooks).isEmpty();
    }

    @Test
    void findByOwnerUsername_whenUserDoesNotExist_thenReturnsEmptyCollection() {
        var notebooks = notebookRepository.findByOwnerUsername(data
                .nonPersistent()
                .appUser()
                .getUsername());

        assertThat(notebooks).isEmpty();
    }

    @Test
    void countSections_whenNotebookContainsSections_thenCountsByNotebookId() {
        Optional<Integer> sectionCount = notebookRepository.countSections(data
                .getNotebookWithSections()
                .getId());

        assertThat(sectionCount).isPresent();

        assertThat(sectionCount.get()).isEqualTo(3);
    }

    @Test
    void countSections_whenNotebookContainsNoSections_thenReturns0() {
        Optional<Integer> sectionCount = notebookRepository.countSections(
                data.getNotebookWithoutSections().getId());

        assertThat(sectionCount).isPresent();

        assertThat(sectionCount.get()).isZero();
    }

    @Test
    void countSections_whenNotebookDoesNotExist_thenReturnsZero() {
        Optional<Integer> sectionCount = notebookRepository.countSections(data
                .nonPersistent()
                .notebook()
                .getId());

        assertThat(sectionCount).isPresent();

        assertThat(sectionCount.get()).isZero();
    }

    @Test
    void countSectionPages_whenNotebookContainsSections_thenCountsPagesOfEachSection() {
        var pageCount = notebookRepository.countSectionPages(
                data.getNotebookWithSections().getId());

        assertThat(pageCount).isPresent();

        assertThat(pageCount.get()).isEqualTo(3);
    }

    @Test
    void countSectionPages_whenNotebookContainsNoSections_thenReturns0() {
        var pageCount = notebookRepository.countSectionPages(
                data.getNotebookWithoutSections().getId());

        assertThat(pageCount).isPresent();

        assertThat(pageCount.get()).isZero();
    }

    @Test
    void countSectionPages_whenNotebookDoesNotExist_thenReturnsZero() {
        var pageCount = notebookRepository.countSectionPages(data
                .nonPersistent()
                .notebook()
                .getId()
        );

        assertThat(pageCount).isPresent();

        assertThat(pageCount.get()).isZero();
    }

}
