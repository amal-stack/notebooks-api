package com.amalstack.api.notebooks.repository;

import com.amalstack.api.notebooks.model.Notebook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.Optional;

public interface NotebookRepository extends JpaRepository<Notebook, Long> {
    Collection<Notebook> findByOwnerId(Long id);

    Collection<Notebook> findByOwnerUsername(String username);

    @Query(value = """
            SELECT COUNT(s.id) FROM Section s
            JOIN Notebook n ON s.notebook_id = n.id
            WHERE n.id = ?1
            """, nativeQuery = true)
    Optional<Integer> countSections(Long id);

    @Query(value = """
            SELECT COUNT(p.id) FROM Page p
            JOIN Section s ON p.section_id = s.id
            JOIN Notebook n ON s.notebook_id = n.id
            WHERE n.id = ?1
            """, nativeQuery = true)
    Optional<Integer> countSectionPages(Long id);
}