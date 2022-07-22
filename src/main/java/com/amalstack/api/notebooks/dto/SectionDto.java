package com.amalstack.api.notebooks.dto;

import com.amalstack.api.notebooks.model.Notebook;
import com.amalstack.api.notebooks.model.Section;

import java.io.Serializable;

public record SectionDto(
        long notebookId,
        String name) implements Serializable {
    public Section toSection(Notebook notebook) {
        return new Section(name, notebook);
    }
}

