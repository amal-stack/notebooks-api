package com.amalstack.api.notebooks.repository;

import com.amalstack.api.notebooks.model.AppUser;
import com.amalstack.api.notebooks.model.Notebook;
import com.amalstack.api.notebooks.model.Page;
import com.amalstack.api.notebooks.model.Section;

import java.time.LocalDateTime;
import java.util.List;

public class TestData {

    public static final String APP_USER_WITH_NOTEBOOKS_USERNAME = "test1@example.com";
    public static final String APP_USER_WITHOUT_NOTEBOOKS_USERNAME = "test2@example.com";

    private final AppUser appUserWithNotebooks = new AppUser(
            APP_USER_WITH_NOTEBOOKS_USERNAME,
            "Test User With Notebooks",
            "password1"
    );
    private final AppUser appUserWithoutNotebooks = new AppUser(
            APP_USER_WITHOUT_NOTEBOOKS_USERNAME,
            "Test User Without Notebooks",
            "password2"
    );

    private final Notebook notebookWithSections = new Notebook("Test Notebook 1",
            "Test Notebook Description",
            LocalDateTime.now(),
            appUserWithNotebooks);
    private final Section sectionWithPages1 = new Section("Test Section 1.1", notebookWithSections);
    private final Page page111 = new Page("Test Page 1.1.1", "Page Contents", sectionWithPages1);
    private final Page page112 = new Page("Test Page 1.1.2", "Page Contents", sectionWithPages1);
    private final List<Page> section1Pages = List.of(page111, page112);
    private final Section sectionWithPages2 = new Section("Test Section 1.2", notebookWithSections);
    private final Page page121 = new Page("Test Page 1.1.3", "Page Contents", sectionWithPages2);
    private final List<Page> section2Pages = List.of(page121);
    private final Section sectionWithoutPages = new Section("Test Section 1.3", notebookWithSections);
    private final Notebook notebookWithNoSections = new Notebook("Test Notebook 2",
            "Test Notebook Description",
            LocalDateTime.now(),
            appUserWithNotebooks);
    private final NonPersistentData nonPersistentData = new NonPersistentData();

    private boolean saved = false;

    public TestData(String randomString) {
        this(randomString, false);
    }

    public TestData(String randomString, boolean setIds) {
        // Prefix each username with the supplied random string
        getAppUsers().forEach(u -> u.setUsername(randomString + u.getUsername()));
        if (setIds) {
            setIds();
        }
    }

    private void setIds() {
        var appUsers = getAppUsers();
        for (int i = 0; i < appUsers.size(); i++) {
            appUsers.get(i).setId(i + 1L);
        }
        var notebooks = getNotebooks();
        for (int i = 0; i < notebooks.size(); i++) {
            notebooks.get(i).setId(i + 1L);
        }

        var sections = getSections();
        for (int i = 0; i < sections.size(); i++) {
            sections.get(i).setId(i + 1L);
        }

        var pages = getPages();
        for (int i = 0; i < pages.size(); i++) {
            pages.get(i).setId(i + 1L);
        }
    }

    public NonPersistentData nonPersistent() {
        return nonPersistentData;
    }

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

    public List<AppUser> getAppUsers() {
        return List.of(appUserWithNotebooks, appUserWithoutNotebooks);
    }

    public List<Notebook> getNotebooks() {
        return List.of(notebookWithSections, notebookWithNoSections);
    }

    public List<Section> getSections() {
        return List.of(sectionWithPages1, sectionWithPages2, sectionWithoutPages);
    }

    public List<Page> getPages() {
        return List.of(page111, page112, page121);
    }

    public void saveToRepositories(AppUserRepository appUserRepository,
                                   NotebookRepository notebookRepository,
                                   SectionRepository sectionRepository,
                                   PageRepository pageRepository) {
        appUserRepository.saveAll(getAppUsers());
        notebookRepository.saveAll(getNotebooks());
        sectionRepository.saveAll(getSections());
        pageRepository.saveAll(getPages());
        saved = true;
    }

    public boolean isSaved() {
        return saved;
    }

    public void initMocks(AppUserRepository appUserRepository,
                          NotebookRepository notebookRepository,
                          SectionRepository sectionRepository,
                          PageRepository pageRepository) {
        var initializer = new MockRepositoryInitializer(this);
        initializer.initMocks(appUserRepository);
        initializer.initMocks(notebookRepository);
        initializer.initMocks(sectionRepository);
        initializer.initMocks(pageRepository);
    }

    public static class NonPersistentData {

        private final AppUser appUser = new AppUser(-1L,
                "nonpersistent@example.com",
                "Non-persistent User",
                "password");
        private final Notebook notebook = new Notebook(-1L,
                "Non-persistent Notebook",
                "Non-persistent Notebook",
                LocalDateTime.now(),
                appUser);
        private final Section section = new Section(-1L,
                "Non-persistent Section",
                notebook);
        private final Page page = new Page(-1L,
                "Non-persistent page",
                "Non-persistent Notebook",
                section);

        private NonPersistentData() {
        }

        public AppUser appUser() {
            return appUser;
        }

        public Notebook notebook() {
            return notebook;
        }

        public Section section() {
            return section;
        }

        public Page page() {
            return page;
        }
    }

}

