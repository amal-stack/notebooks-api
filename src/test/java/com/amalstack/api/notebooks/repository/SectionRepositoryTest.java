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
    @Autowired private SectionRepository sectionRepository;

    @Autowired private AppUserRepository appUserRepository;

    @Autowired private NotebookRepository notebookRepository;

    private TestData data;
    private long notebookId;

    @BeforeAll
    void init() {
        data = new TestData();
        appUserRepository.save(data.getAppUserWithNotebooks());

        notebookId = notebookRepository.save(data.getNotebookWithSections()).getId();

        sectionRepository.save(data.getSection1WithPages());
        sectionRepository.save(data.getSection2WithPages());
        sectionRepository.save(data.getSectionWithoutPages());
    }

    @Test
    void findByNotebookId_whenNotebookExists_thenFindsSectionsByNotebookId() {
        Collection<Section> sections = sectionRepository.findByNotebookId(notebookId);
        assertThat(sections).hasSize(3);
        assertThat(sections)
                .extracting(s -> s.getNotebook().getId())
                .allMatch(i -> i.equals(notebookId));
    }

    @Test
    void findByNotebookId_whenNotebookDoesNotExist_thenReturnsEmptyCollection() {
        Collection<Section> sections = sectionRepository.findByNotebookId(
                data.getNotebookWithoutSections().getId());

        assertThat(sections).isEmpty();
    }

    @Test
    void countByNotebookId_whenNotebookExists_thenCountsSectionsByNotebookId() {
        var sectionCount = sectionRepository.countByNotebookId(notebookId);
        assertThat(sectionCount).isEqualTo(3);
    }

    @Test
    void countByNotebookId_whenNotebookDoesNotExist_thenReturns0() {
        var sectionCount = sectionRepository.countByNotebookId(
                data.getNotebookWithoutSections().getId()
        );
        assertThat(sectionCount).isZero();
    }
}