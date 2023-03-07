package com.amalstack.api.notebooks.dto;

import com.amalstack.api.notebooks.model.Page;
import com.amalstack.api.notebooks.model.Section;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

public record PageDto(
        long sectionId,
        @NotBlank String title,
        String content) implements Serializable {

    public Page toPage(Section section) {
        return new Page(title, content, section);
    }
}


