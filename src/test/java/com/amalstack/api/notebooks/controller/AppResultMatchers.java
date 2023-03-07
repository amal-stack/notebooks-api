package com.amalstack.api.notebooks.controller;

import com.amalstack.api.notebooks.exception.NotebookNotFoundByIdException;
import com.amalstack.api.notebooks.exception.PageNotFoundByIdException;
import com.amalstack.api.notebooks.exception.ResourceNotOwnedException;
import com.amalstack.api.notebooks.exception.SectionNotFoundByIdException;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AppResultMatchers {
    public static ResultMatcher resourceIsNotOwned(Long resourceId, String resourceName) {
        return result -> {
            status().isUnauthorized().match(result);
            status().reason("Current user does not have the ownership of the resource").match(result);
            assertThat(result.getResolvedException())
                    .isInstanceOf(ResourceNotOwnedException.class)
                    .hasMessage("Current user does not own the %s with id %d",
                            resourceName, resourceId);
        };
    }

    public static ResultMatcher notebookIsNotFoundById(Long notebookId) {
        return result -> {
            status().isNotFound().match(result);
            status().reason("Notebook not found").match(result);
            assertThat(result.getResolvedException())
                    .isInstanceOf(NotebookNotFoundByIdException.class)
                    .hasMessage("The notebook with id %d was not found",
                            notebookId);
        };
    }

    public static ResultMatcher sectionIsNotFoundById(Long sectionId) {
        return result -> {
            status()
                    .isNotFound()
                    .match(result);
            status()
                    .reason("Section not found")
                    .match(result);

            AppResultMatchers.resolvedException()
                    .isInstanceOf(SectionNotFoundByIdException.class)
                    .hasMessage("The section with id %d was not found", sectionId)
                    .hasFieldOrPropertyWithValue("id", sectionId)
                    .match(result);
        };
    }

    public static ResultMatcher pageIsNotFoundById(Long pageId) {
        return result -> {
            status()
                    .isNotFound()
                    .match(result);
            status()
                    .reason("Page not found")
                    .match(result);

            AppResultMatchers.resolvedException()
                    .isInstanceOf(PageNotFoundByIdException.class)
                    .hasMessage("The page with id %d was not found", pageId)
                    .hasFieldOrPropertyWithValue("id", pageId)
                    .match(result);
        };
    }

    public static FailedValidationResultMatcher validationFailed() {
        return new FailedValidationResultMatcher();
    }

    public static ResolvedExceptionResultMatcher resolvedException() {
        return new ResolvedExceptionResultMatcher();
    }
}

