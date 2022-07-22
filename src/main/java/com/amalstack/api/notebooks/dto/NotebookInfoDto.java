package com.amalstack.api.notebooks.dto;

import com.amalstack.api.notebooks.model.Notebook;

public record NotebookInfoDto(
        long id,
        String name,
        String description,
        long userId,
        String username
) {
    public static NotebookInfoDto fromNotebook(Notebook notebook) {
        var owner = notebook.getOwner();
        return new NotebookInfoDto(notebook.getId(), notebook.getName(), notebook.getDescription(), owner.getId(), owner.getUsername());
    }
}
