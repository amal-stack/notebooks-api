package com.amalstack.api.notebooks.model;

import javax.persistence.*;

@Entity
public class Section {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "notebook_id")
    private Notebook notebook;

    public Section() {
    }

    public Section(long id, String name, Notebook notebook) {

        this.id = id;
        this.name = name;
        this.notebook = notebook;
    }

    public Section(String name, Notebook notebook) {

        this.name = name;
        this.notebook = notebook;
    }

    public Notebook getNotebook() {
        return notebook;
    }

    public void setNotebook(Notebook notebook) {
        this.notebook = notebook;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", notebook=" + notebook +
                '}';
    }
}
