package com.amalstack.api.notebooks.repository;

import com.amalstack.api.notebooks.model.Section;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SectionRepositoryTest {

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private NotebookRepository notebookRepository;

    @Autowired
    private PageRepository pageRepository;

    private TestData data;
    private long notebookId;

    @BeforeAll
    void init() {
        data = new TestData("SectionRepositoryTest");
        data.saveToRepositories(appUserRepository, notebookRepository, sectionRepository, pageRepository);

        notebookId = notebookRepository.save(
                        data.getNotebookWithSections())
                .getId();
    }

    @Test
    void findByNotebookId_whenNotebookContainsSections_thenFindsSectionsByNotebookId() {
        Collection<Section> sections = sectionRepository.findByNotebookId(notebookId);

        assertThat(sections).hasSize(data.getSections().size());
        assertThat(sections)
                .extracting(s -> s.getNotebook().getId())
                .allMatch(i -> i.equals(notebookId));
    }

    @Test
    void findByNotebookId_whenNotebookContainsNoSections_thenReturnsEmptyCollection() {
        Collection<Section> sections = sectionRepository.findByNotebookId(
                data.getNotebookWithoutSections().getId());

        assertThat(sections).isEmpty();
    }

    @Test
    void findByNotebookId_whenNotebookDoesNotExist_thenReturnsEmptyCollection() {
        Collection<Section> sections = sectionRepository.findByNotebookId(data
                .nonPersistent()
                .notebook()
                .getId());

        assertThat(sections).isEmpty();
    }

    @Test
    void countByNotebookId_whenNotebookContainsSections_thenCountsSectionsByNotebookId() {
        var sectionCount = sectionRepository.countByNotebookId(notebookId);

        assertThat(sectionCount).isEqualTo(data.getSections().size());
    }

    @Test
    void countByNotebookId_whenNotebookContainsNoSections_thenReturns0() {
        var sectionCount = sectionRepository.countByNotebookId(
                data.getNotebookWithoutSections().getId()
        );

        assertThat(sectionCount).isZero();
    }

    @Test
    void countByNotebookId_whenNotebookDoesNotExist_thenReturns0() {
        var sectionCount = sectionRepository.countByNotebookId(data
                .nonPersistent()
                .notebook()
                .getId()
        );

        assertThat(sectionCount).isZero();
    }
}