package com.amalstack.api.notebooks.dto;

import com.amalstack.api.notebooks.model.AppUser;
import com.amalstack.api.notebooks.model.Notebook;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;
import java.time.LocalDateTime;

public record NotebookDto(
        @NotBlank String name,
        String description) implements Serializable {

    public Notebook toNotebook(AppUser user) {
        return new Notebook(name, description, LocalDateTime.now(), user);
    }
}

