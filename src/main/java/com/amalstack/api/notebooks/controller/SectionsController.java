package com.amalstack.api.notebooks.controller;

import com.amalstack.api.notebooks.dto.SectionDto;
import com.amalstack.api.notebooks.dto.SectionSummaryDto;
import com.amalstack.api.notebooks.exception.NotebookNotFoundByIdException;
import com.amalstack.api.notebooks.exception.SectionNotFoundByIdException;
import com.amalstack.api.notebooks.model.Notebook;
import com.amalstack.api.notebooks.model.Section;
import com.amalstack.api.notebooks.repository.NotebookRepository;
import com.amalstack.api.notebooks.repository.SectionRepository;
import com.amalstack.api.notebooks.validation.OwnershipGuard;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping(value = "sections", produces = MediaType.APPLICATION_JSON_VALUE)
public class SectionsController {
    private final SectionRepository sectionRepository;
    private final NotebookRepository notebookRepository;

    public SectionsController(SectionRepository sectionRepository, NotebookRepository notebookRepository) {
        this.sectionRepository = sectionRepository;
        this.notebookRepository = notebookRepository;
    }

    @GetMapping("/{id}")
    public SectionSummaryDto get(@PathVariable long id, @AuthenticationPrincipal User user) {
        return sectionRepository
                .findById(id)
                .map(section -> {
                    OwnershipGuard.throwIfSectionNotOwned(user, section);
                    return SectionSummaryDto.fromSection(section);
                })
                .orElseThrow(() -> new SectionNotFoundByIdException(id));
    }

    @GetMapping("/notebook/{id}")
    public Collection<SectionSummaryDto> getByNotebookId(@PathVariable long id, @AuthenticationPrincipal User user) {
        Notebook notebook = notebookRepository
                .findById(id)
                .orElseThrow(() -> new NotebookNotFoundByIdException(id));

        OwnershipGuard.throwIfNotebookNotOwned(user, notebook);

        return sectionRepository
                .findByNotebookId(id)
                .stream()
                .map(SectionSummaryDto::fromSection)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SectionSummaryDto create(@RequestBody @Valid SectionDto sectionDto, @AuthenticationPrincipal User user) {
        var notebookId = sectionDto.notebookId();
        Notebook notebook = notebookRepository
                .findById(notebookId)
                .orElseThrow(() -> new NotebookNotFoundByIdException(notebookId));

        OwnershipGuard.throwIfNotebookNotOwned(user, notebook);

        Section section = sectionDto.toSection(notebook);

        return SectionSummaryDto.fromSection(sectionRepository.save(section));
    }

    @PutMapping("/{id}")
    public SectionSummaryDto update(@PathVariable long id,
                                    @RequestBody @Valid SectionDto sectionDto,
                                    @AuthenticationPrincipal User user) {

        var notebookId = sectionDto.notebookId();
        Notebook notebook = notebookRepository
                .findById(notebookId)
                .orElseThrow(() -> new NotebookNotFoundByIdException(notebookId));

        OwnershipGuard.throwIfNotebookNotOwned(user, notebook);

        Section section = sectionRepository
                .findById(id)
                .map(sec -> {
                    OwnershipGuard.throwIfSectionNotOwned(user, sec);
                    sec.setName(sectionDto.name());
                    if (sec.getNotebook().getId() != notebook.getId()) {
                        sec.setNotebook(notebook);
                    }
                    return sec;
                })
                .orElseGet(() -> {
                    var sec = sectionDto.toSection(notebook);
                    sec.setId(id);
                    return sec;
                });

        return SectionSummaryDto.fromSection(sectionRepository.save(section));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id, @AuthenticationPrincipal User user) {
        Section section = sectionRepository
                .findById(id)
                .orElseThrow(() -> new SectionNotFoundByIdException(id));

        OwnershipGuard.throwIfSectionNotOwned(user, section);

        sectionRepository.deleteById(id);
    }
}
