package com.amalstack.api.notebooks.repository;

import com.amalstack.api.notebooks.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface SectionRepository extends JpaRepository<Section, Long> {
    Collection<Section> findByNotebookId(long id);
}