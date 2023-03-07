package com.amalstack.api.notebooks.controller;

import com.amalstack.api.notebooks.dto.PageDto;
import com.amalstack.api.notebooks.dto.PageInfoDto;
import com.amalstack.api.notebooks.exception.PageNotFoundByIdException;
import com.amalstack.api.notebooks.exception.SectionNotFoundByIdException;
import com.amalstack.api.notebooks.model.Page;
import com.amalstack.api.notebooks.model.Section;
import com.amalstack.api.notebooks.repository.PageRepository;
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
@RequestMapping(value = "pages", produces = MediaType.APPLICATION_JSON_VALUE)
public class PagesController {
    private final PageRepository pageRepository;
    private final SectionRepository sectionRepository;

    public PagesController(PageRepository pageRepository, SectionRepository sectionRepository) {
        this.pageRepository = pageRepository;
        this.sectionRepository = sectionRepository;
    }

    @GetMapping("/{id}")
    public PageInfoDto get(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return pageRepository
                .findById(id)
                .map(page -> {
                    OwnershipGuard.throwIfPageNotOwned(user, page);
                    return PageInfoDto.fromPage(page);
                })
                .orElseThrow(() -> new PageNotFoundByIdException(id));
    }

    @GetMapping("/section/{id}")
    public Collection<PageInfoDto> getBySection(@PathVariable long id, @AuthenticationPrincipal User user) {
        Section section = sectionRepository
                .findById(id)
                .orElseThrow(() -> new SectionNotFoundByIdException(id));

        OwnershipGuard.throwIfSectionNotOwned(user, section);

        return pageRepository
                .findBySectionId(id)
                .stream()
                .map(PageInfoDto::fromPage)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PageInfoDto create(@RequestBody @Valid PageDto pageDto, @AuthenticationPrincipal User user) {
        var sectionId = pageDto.sectionId();

        Section section = sectionRepository
                .findById(sectionId)
                .orElseThrow(() -> new SectionNotFoundByIdException(sectionId));

        OwnershipGuard.throwIfSectionNotOwned(user, section);

        var page = pageDto.toPage(section);
        return PageInfoDto.fromPage(pageRepository.save(page));
    }

    @PutMapping("/{id}")
    public PageInfoDto update(@PathVariable long id,
                              @RequestBody @Valid PageDto pageDto,
                              @AuthenticationPrincipal User user) {

        Section section = sectionRepository
                .findById(pageDto.sectionId())
                .orElseThrow(() -> new SectionNotFoundByIdException(pageDto.sectionId()));

        OwnershipGuard.throwIfSectionNotOwned(user, section);

        Page page = pageRepository
                .findById(id)
                .map(p -> {
                    OwnershipGuard.throwIfPageNotOwned(user, p);
                    p.setContent(pageDto.content());
                    p.setTitle(pageDto.title());
                    // update section if different
                    if (!p.getSection().getId().equals(section.getId())) {
                        p.setSection(section);
                    }
                    return p;
                })
                .orElseGet(() -> {
                    var p = pageDto.toPage(section);
                    p.setId(id);
                    return p;
                });

        return PageInfoDto.fromPage(pageRepository.save(page));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id, @AuthenticationPrincipal User user) {
        Page page = pageRepository
                .findById(id)
                .orElseThrow(() -> new PageNotFoundByIdException(id));

        OwnershipGuard.throwIfPageNotOwned(user, page);

        pageRepository.deleteById(id);
    }
}
