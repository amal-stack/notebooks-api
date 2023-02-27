package com.amalstack.api.notebooks.repository;

import com.amalstack.api.notebooks.model.AppUser;
import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.Optional;

public class MockRepositoryInitializer extends MockRepositoryInitializerBase {
    MockRepositoryInitializer(TestData testData) {
        super(testData);
    }

    public void initMocks(AppUserRepository appUserRepository) {
        Mockito
                .when(appUserRepository.findByUsername(testData.getAppUserWithNotebooks().getUsername()))
                .thenReturn(Optional.of(testData.getAppUserWithNotebooks()));

        Mockito.when(appUserRepository.save(Mockito.any(AppUser.class)))
                .then(AdditionalAnswers.returnsFirstArg());

        super.initMocks(appUserRepository);
    }

    public void initMocks(NotebookRepository notebookRepository) {

        Mockito.when(notebookRepository
                        .findById(testData.getNotebookWithSections().getId()))
                .thenReturn(Optional
                        .of(testData.getNotebookWithSections()));
        Mockito.when(notebookRepository
                        .findById(testData.getNotebookWithoutSections().getId()))
                .thenReturn(Optional
                        .of(testData.getNotebookWithoutSections()));

        Mockito.when(notebookRepository
                        .findByOwnerUsername(testData.getAppUserWithNotebooks().getUsername()))
                .thenReturn(testData.getNotebooks());
        Mockito.when(notebookRepository
                        .findByOwnerUsername(testData.getAppUserWithoutNotebooks().getUsername()))
                .thenReturn(Collections.emptyList());

        Mockito.when(notebookRepository
                        .countSections(testData.getNotebookWithSections().getId()))
                .thenReturn(Optional.of(testData.getSections().size()));

        Mockito.when(notebookRepository
                        .countSectionPages(testData.getNotebookWithSections().getId()))
                .thenReturn(Optional.of(testData.getPages().size()));

        Mockito.when(notebookRepository
                        .countSections(testData.getNotebookWithoutSections().getId()))
                .thenReturn(Optional.of(0));

        Mockito.when(notebookRepository
                        .countSectionPages(testData.getNotebookWithoutSections().getId()))
                .thenReturn(Optional.of(0));

        super.initMocks(notebookRepository);
    }

    public void initMocks(SectionRepository sectionRepository) {
        Mockito.when(sectionRepository
                        .findByNotebookId(testData.getNotebookWithSections().getId()))
                .thenReturn(testData.getSections());
        Mockito.when(sectionRepository
                        .findByNotebookId(testData.getNotebookWithoutSections().getId()))
                .thenReturn(Collections.emptyList());

        super.initMocks(sectionRepository);
    }

    public void initMocks(PageRepository pageRepository) {
        Mockito.when(pageRepository
                        .findBySectionId(testData.getSection1WithPages().getId()))
                .thenReturn(testData.getSection1Pages());
        Mockito.when(pageRepository
                        .findBySectionId(testData.getSection2WithPages().getId()))
                .thenReturn(testData.getSection2Pages());
        Mockito.when(pageRepository
                        .findBySectionId(testData.getSectionWithoutPages().getId()))
                .thenReturn(Collections.emptyList());

        super.initMocks(pageRepository);
    }


}
