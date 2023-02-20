package com.amalstack.api.notebooks.repository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PageRepositoryTest {

    @Autowired
    private PageRepository pageRepository;

    @Autowired
    private NotebookRepository notebookRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    private TestData data;

    private Long section1Id;


    @BeforeAll
    void init() {
        data = new TestData("PageRepositoryTest");
        data.saveToRepositories(appUserRepository, notebookRepository, sectionRepository, pageRepository);
        section1Id = data.getSection1WithPages().getId();
    }

    @Test
    void findBySectionId_whenSectionContainsPages_thenFindsPagesBySectionId() {
        var pages = pageRepository.findBySectionId(section1Id);

        assertThat(pages).hasSize(data.getSection1Pages().size());

        assertThat(pages)
                .extracting(p -> p.getSection().getId())
                .allMatch(i -> section1Id.equals(i));
    }

    @Test
    void findBySectionId_whenSectionContainsNoPages_thenReturnsEmptyCollection() {
        var pages = pageRepository.findBySectionId(
                data.getSectionWithoutPages().getId());

        assertThat(pages).isEmpty();
    }

    @Test
    void findBySectionId_whenSectionDoesNotExist_thenReturnsEmptyCollection() {
        var pages = pageRepository.findBySectionId(data
                .nonPersistent()
                .section()
                .getId());

        assertThat(pages).isEmpty();
    }

    @Test
    void countBySectionId_whenSectionContainsPages_thenCountsPagesBySectionId() {
        var count = pageRepository.countBySectionId(section1Id);

        assertThat(count).isEqualTo(data.getSection1Pages().size());
    }

    @Test
    void countBySectionId_whenSectionContainsNoPages_thenReturns0() {
        var count = pageRepository.countBySectionId(data
                .getSectionWithoutPages().getId());

        assertThat(count).isZero();
    }

    @Test
    void countBySectionId_whenSectionDoesNotExist_thenReturns0() {
        var count = pageRepository.countBySectionId(data
                .nonPersistent()
                .section()
                .getId());

        assertThat(count).isZero();
    }

    @Test
    void countBySectionNotebookId_whenNotebookContainsSections_thenCountsPagesOfAllSectionsByNotebookId() {
        var count = pageRepository.countBySectionNotebookId(data
                .getNotebookWithSections()
                .getId());
        // Both the below sections belong to the notebook
        var expected = data.getSection1Pages().size() + data.getSection2Pages().size();

        assertThat(count).isEqualTo(expected);
    }

    @Test
    void countBySectionNotebookId_whenNotebookContainsNoSections_thenReturns0() {
        var count = pageRepository.countBySectionNotebookId(data
                .getNotebookWithoutSections()
                .getId());

        assertThat(count).isZero();
    }

    @Test
    void countBySectionNotebookId_whenNotebookDoesNotExist_thenReturns0() {
        var count = pageRepository.countBySectionNotebookId(data
                .nonPersistent()
                .notebook()
                .getId());

        assertThat(count).isZero();
    }

}