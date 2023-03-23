package com.amalstack.api.notebooks.model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
public class Page {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private Long id;

    private String title;

    private String content;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "section_id")
    private Section section;

    public Page() {

    }

    public Page(Long id, String title, String content, Section section) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.section = section;
    }

    public Page(String title, String content, Section section) {
        this.title = title;
        this.content = content;
        this.section = section;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

}
