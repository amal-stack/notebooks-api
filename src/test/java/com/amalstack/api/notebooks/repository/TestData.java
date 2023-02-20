package com.amalstack.api.notebooks.repository;

import com.amalstack.api.notebooks.model.AppUser;
import com.amalstack.api.notebooks.model.Notebook;
import com.amalstack.api.notebooks.model.Page;
import com.amalstack.api.notebooks.model.Section;

import java.time.LocalDateTime;
import java.util.List;

class TestData {
    private final AppUser appUserWithNotebooks = new AppUser(
            "test1@example.com",
            "Test User With Notebooks",
            "password1"
    );

    private final AppUser appUserWithoutNotebooks = new AppUser(
            "test2@example.com",
            "Test User Without Notebooks",
            "password2"
    );

    private final Notebook notebookWithSections = new Notebook("Test Notebook 1",
            "Test Notebook Description",
            LocalDateTime.now(),
            appUserWithNotebooks);

    private final Notebook notebookWithNoSections = new Notebook("Test Notebook 2",
            "Test Notebook Description",
            LocalDateTime.now(),
            appUserWithNotebooks);

    private final Section sectionWithPages1 = new Section("Test Section 1.1", notebookWithSections);
    private final Section sectionWithPages2 = new Section("Test Section 1.2", notebookWithSections);
    private final Section sectionWithoutPages = new Section("Test Section 1.3", notebookWithSections);


    private final Page page111 = new Page("Test Page 1.1.1", "Page Contents", sectionWithPages1);
    private final Page page112 = new Page("Test Page 1.1.2", "Page Contents", sectionWithPages1);
    private final Page page121 = new Page("Test Page 1.1.3", "Page Contents", sectionWithPages2);

    private final List<Page> section1Pages = List.of(page111, page112);
    private final List<Page> section2Pages = List.of(page121);

    public AppUser getAppUserWithNotebooks() {
        return appUserWithNotebooks;
    }

    public AppUser getAppUserWithoutNotebooks() {
        return appUserWithoutNotebooks;
    }

    public Notebook getNotebookWithSections() {
        return notebookWithSections;
    }

    public Notebook getNotebookWithoutSections() {
        return notebookWithNoSections;
    }

    public Section getSection1WithPages() {
        return sectionWithPages1;
    }

    public Section getSection2WithPages() {
        return sectionWithPages2;
    }

    public Section getSectionWithoutPages() {
        return sectionWithoutPages;
    }

    public List<Page> getSection1Pages() {
        return section1Pages;
    }

    public List<Page> getSection2Pages() {
        return section2Pages;
    }
}
