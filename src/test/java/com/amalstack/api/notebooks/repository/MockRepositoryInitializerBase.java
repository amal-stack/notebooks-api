package com.amalstack.api.notebooks.repository;

import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;
import org.springframework.data.jpa.repository.JpaRepository;

public abstract class MockRepositoryInitializerBase {
    protected final TestData testData;

    MockRepositoryInitializerBase(TestData testData) {
        this.testData = testData;
    }

    public <T, ID> void initMocks(JpaRepository<T, ID> repository) {
        Mockito
                .when(repository.save(Mockito.any()))
                .then(AdditionalAnswers.returnsFirstArg());
    }
}
