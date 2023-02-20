package com.amalstack.api.notebooks.repository;

import com.amalstack.api.notebooks.model.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface PageRepository extends JpaRepository<Page, Long> {
    Collection<Page> findBySectionId(Long id);

    int countBySectionId(Long id);

    int countBySectionNotebookId(Long id);
}