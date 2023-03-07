package com.amalstack.api.notebooks.dto;

import com.amalstack.api.notebooks.model.Notebook;
import com.amalstack.api.notebooks.model.Section;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

public record SectionDto(
        long notebookId,
        @NotBlank String name) implements Serializable {
    public Section toSection(Notebook notebook) {
        return new Section(name, notebook);
    }
}

