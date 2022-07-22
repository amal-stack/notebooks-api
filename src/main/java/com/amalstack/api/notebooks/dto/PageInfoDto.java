package com.amalstack.api.notebooks.dto;

import com.amalstack.api.notebooks.model.Page;

import java.io.Serializable;

public record PageInfoDto(
        long id,
        String title,
        String content,
        long sectionId
) implements Serializable {
    public static PageInfoDto fromPage(Page page) {
        return new PageInfoDto(page.getId(), page.getTitle(), page.getContent(), page.getSection().getId());
    }
}
