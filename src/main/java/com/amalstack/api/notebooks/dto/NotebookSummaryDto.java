package com.amalstack.api.notebooks.dto;

import com.amalstack.api.notebooks.model.Notebook;

import java.io.Serializable;
import java.time.LocalDateTime;

public record NotebookSummaryDto(
        long id,
        String name,
        String description,
        LocalDateTime creationTime,
        int sectionCount,
        int pageCount,
        long userId,
        String username
) implements Serializable {
    public static NotebookSummaryDto fromNotebook(Notebook notebook, int sectionCount, int pageCount) {
        var owner = notebook.getOwner();
        return new NotebookSummaryDto(notebook.getId(),
                notebook.getName(),
                notebook.getDescription(),
                notebook.getCreationTime(),
                sectionCount,
                pageCount,
                owner.getId(),
                owner.getUsername());
    }

    public static NotebookSummaryDto fromNotebook(Notebook notebook) {
        return fromNotebook(notebook, 0, 0);
    }
}

