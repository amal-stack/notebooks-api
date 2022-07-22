package com.amalstack.api.notebooks.controller;

import com.amalstack.api.notebooks.exception.AppUserNotFoundException;
import com.amalstack.api.notebooks.exception.NotebookNotFoundByIdException;
import com.amalstack.api.notebooks.exception.OwnershipGuard;
import com.amalstack.api.notebooks.model.Notebook;
import com.amalstack.api.notebooks.dto.NotebookDto;
import com.amalstack.api.notebooks.dto.NotebookInfoDto;
import com.amalstack.api.notebooks.repository.AppUserRepository;
import com.amalstack.api.notebooks.repository.NotebookRepository;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping(value = "notebooks", produces = MediaType.APPLICATION_JSON_VALUE)
public class NotebooksController {
    private final NotebookRepository notebookRepository;
    private final AppUserRepository userRepository;

    public NotebooksController(NotebookRepository notebookRepository, AppUserRepository userRepository) {
        this.notebookRepository = notebookRepository;
        this.userRepository = userRepository;
    }


    @GetMapping("/{id}")
    public NotebookInfoDto get(@PathVariable long id, @AuthenticationPrincipal User user) {

        return notebookRepository
                .findById(id)
                .map(notebook -> {
                    OwnershipGuard.throwIfNotebookNotOwned(user, notebook);
                    return NotebookInfoDto.fromNotebook(notebook);
                })
                .orElseThrow(() -> new NotebookNotFoundByIdException(id));
    }

    @GetMapping("/user")
    public Collection<NotebookInfoDto> getByUser(@AuthenticationPrincipal User user) {
        return notebookRepository
                .findByOwnerUsername(user.getUsername())
                .stream()
                .map(NotebookInfoDto::fromNotebook)
                .toList();
    }

    @PostMapping
    public NotebookInfoDto create(@Valid @RequestBody NotebookDto notebookDto, @AuthenticationPrincipal User user) {
        var username = user.getUsername();
        var appUser = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new AppUserNotFoundException(
                        "The user with username " + username + " was not found"
                ));

        Notebook savedNotebook = notebookRepository.save(notebookDto.toNotebook(appUser));

        return NotebookInfoDto.fromNotebook(savedNotebook);
    }

    @PutMapping("/{id}")
    public NotebookInfoDto update(@PathVariable long id,
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
        return NotebookInfoDto.fromNotebook(savedNotebook);
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
