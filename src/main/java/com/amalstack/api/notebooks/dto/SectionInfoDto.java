package com.amalstack.api.notebooks.dto;

import com.amalstack.api.notebooks.model.Section;

import java.io.Serializable;
import java.util.Collection;

public record SectionInfoDto(
        long id,
        long notebookId,
        String name,
        Collection<PageInfoDto> pages) implements Serializable {

    public static SectionInfoDto fromSection(Section section, Collection<PageInfoDto> pages) {
        return new SectionInfoDto(section.getId(),
                section.getNotebook().getId(),
                section.getName(),
                pages);
    }
}
