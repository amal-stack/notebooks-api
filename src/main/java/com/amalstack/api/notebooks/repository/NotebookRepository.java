package com.amalstack.api.notebooks.repository;

import com.amalstack.api.notebooks.model.Notebook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface NotebookRepository extends JpaRepository<Notebook, Long> {
    Collection<Notebook> findByOwnerId(Long id);
    Collection<Notebook> findByOwnerUsername(String username);
}