package com.amalstack.api.notebooks.dto;

import com.amalstack.api.notebooks.model.Notebook;

import java.time.LocalDateTime;
import java.util.Collection;

public record NotebookInfoDto(
        long id,
        long userId,
        String name,
        String description,
        LocalDateTime creationTime,
        Collection<SectionInfoDto> sections) {

    public static NotebookInfoDto fromNotebook(Notebook notebook, Collection<SectionInfoDto> sections) {
        return new NotebookInfoDto(notebook.getId(),
                notebook.getOwner().getId(),
                notebook.getName(),
                notebook.getDescription(),
                notebook.getCreationTime(),
                sections);
    }
}
