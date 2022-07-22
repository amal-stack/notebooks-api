package com.amalstack.api.notebooks.exception;

import com.amalstack.api.notebooks.model.Notebook;
import com.amalstack.api.notebooks.model.Page;
import com.amalstack.api.notebooks.model.Section;
import org.springframework.security.core.userdetails.User;

public final class OwnershipGuard {

    public static void throwIfNotebookNotOwned(User user, Notebook notebook) {
        if (isNotebookNotOwned(user, notebook)) {
            throw new ResourceNotOwnedException("Current user does not own the notebook with id " + notebook.getId());
        }
    }

    public static void throwIfPageNotOwned(User user, Page page) {
        if (isNotebookNotOwned(user, page.getSection().getNotebook())) {
            throw new ResourceNotOwnedException("Current user does not own the page with id " + page.getId());
        }
    }

    public static void throwIfSectionNotOwned(User user, Section section) {
        if (isNotebookNotOwned(user, section.getNotebook())) {
            throw new ResourceNotOwnedException("Current user does not own the section with id " + section.getId());
        }
    }

    private static boolean isNotebookNotOwned(User user, Notebook notebook) {
        return !notebook
                .getOwner()
                .getUsername()
                .equals(user.getUsername());
    }

    private OwnershipGuard() {

    }
}
