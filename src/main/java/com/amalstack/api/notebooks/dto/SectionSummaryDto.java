package com.amalstack.api.notebooks.dto;

import com.amalstack.api.notebooks.model.Section;

import java.io.Serializable;

public record SectionSummaryDto(
        long id,
        String name,
        long notebookId) implements Serializable {
    public static SectionSummaryDto fromSection(Section section) {
        return new SectionSummaryDto(section.getId(), section.getName(), section.getNotebook().getId());
    }
}

