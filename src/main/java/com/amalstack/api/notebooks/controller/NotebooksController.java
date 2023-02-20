package com.amalstack.api.notebooks.controller;

import com.amalstack.api.notebooks.dto.*;
import com.amalstack.api.notebooks.exception.AppUserNotFoundException;
import com.amalstack.api.notebooks.exception.NotebookNotFoundByIdException;
import com.amalstack.api.notebooks.model.Notebook;
import com.amalstack.api.notebooks.model.Section;
import com.amalstack.api.notebooks.repository.AppUserRepository;
import com.amalstack.api.notebooks.repository.NotebookRepository;
import com.amalstack.api.notebooks.repository.PageRepository;
import com.amalstack.api.notebooks.repository.SectionRepository;
import com.amalstack.api.notebooks.validation.OwnershipGuard;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping(value = "notebooks", produces = MediaType.APPLICATION_JSON_VALUE)
public class NotebooksController {
    private final NotebookRepository notebookRepository;
    private final AppUserRepository userRepository;
    private final SectionRepository sectionRepository;
    private final PageRepository pageRepository;

    public NotebooksController(NotebookRepository notebookRepository, AppUserRepository userRepository,
                               SectionRepository sectionRepository,
                               PageRepository pageRepository) {
        this.notebookRepository = notebookRepository;
        this.userRepository = userRepository;
        this.sectionRepository = sectionRepository;
        this.pageRepository = pageRepository;
    }


    @GetMapping("/{id}")
    public NotebookInfoDto get(@PathVariable long id, @AuthenticationPrincipal User user) {
        Collection<Section> sections = sectionRepository.findByNotebookId(id);
        List<SectionInfoDto> sectionInfo = new ArrayList<>();
        sections.forEach(section -> {
            Collection<PageInfoDto> pages = pageRepository.findBySectionId(section.getId())
                    .stream()
                    .map(PageInfoDto::fromPage)
                    .toList();
            sectionInfo.add(SectionInfoDto.fromSection(section, pages));
        });
        return notebookRepository
                .findById(id)
                .map(notebook -> {
                    OwnershipGuard.throwIfNotebookNotOwned(user, notebook);
                    return NotebookInfoDto.fromNotebook(notebook, sectionInfo);
                })
                .orElseThrow(() -> new NotebookNotFoundByIdException(id));
    }

    @GetMapping("/user")
    public Collection<NotebookSummaryDto> getByUser(@AuthenticationPrincipal User user) {
        return notebookRepository
                .findByOwnerUsername(user.getUsername())
                .stream()
                .map(notebook -> {
                    int sections = notebookRepository.countSections(notebook.getId()).orElseThrow();
                    int pages = notebookRepository.countSectionPages(notebook.getId()).orElseThrow();
                    return NotebookSummaryDto.fromNotebook(notebook, sections, pages);
                })
                .toList();
    }

    @PostMapping
    public NotebookSummaryDto create(@Valid @RequestBody NotebookDto notebookDto, @AuthenticationPrincipal User user) {
        var username = user.getUsername();
        var appUser = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new AppUserNotFoundException(
                        "The user with username " + username + " was not found"
                ));

        Notebook savedNotebook = notebookRepository.save(notebookDto.toNotebook(appUser));

        return NotebookSummaryDto.fromNotebook(savedNotebook);
    }

    @PutMapping("/{id}")
    public NotebookSummaryDto update(@PathVariable long id,
                                     @Valid @RequestBody NotebookDto notebookDto,
                                     @AuthenticationPrincipal User user) {

        // Get or create notebook
        Notebook notebook = notebookRepository
                .findById(id)
                .map(nb -> {
                    nb.setName(notebookDto.name());
                    nb.setDescription(notebookDto.description());
                    return nb;
                })
                .orElse(notebookDto.toNotebook(userRepository
                        .findByUsername(user.getUsername())
                        .orElseThrow(AppUserNotFoundException::new)));

        OwnershipGuard.throwIfNotebookNotOwned(user, notebook);

        var savedNotebook = notebookRepository.save(notebook);
        return NotebookSummaryDto.fromNotebook(savedNotebook);
    }


    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, @AuthenticationPrincipal User user) {
        var notebook = notebookRepository
                .findById(id)
                .orElseThrow(() -> new NotebookNotFoundByIdException(id));
        OwnershipGuard.throwIfNotebookNotOwned(user, notebook);
        notebookRepository.deleteById(id);
    }

}
