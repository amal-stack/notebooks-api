package com.amalstack.api.notebooks.dto;

import com.amalstack.api.notebooks.model.Section;

import java.io.Serializable;

public record SectionInfoDto(
        long id,
        String name,
        long notebookId) implements Serializable {
    public static SectionInfoDto fromSection(Section section) {
        return new SectionInfoDto(section.getId(), section.getName(), section.getNotebook().getId());
    }
}
