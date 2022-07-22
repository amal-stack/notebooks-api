package com.amalstack.api.notebooks.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class Notebook {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private long id;

    @Column(nullable = false)
    private String name;


    private String description;

    private LocalDateTime creationTime;

    @ManyToOne
    @JoinColumn(name = "app_user_id")
    private AppUser owner;

    public Notebook() {
    }

    public Notebook(long id, String name, String description, LocalDateTime creationTime, AppUser owner) {
        this.id = id;
        this.name = name;
        this.creationTime = creationTime;
        this.owner = owner;
    }

    public Notebook(String name, String description, LocalDateTime creationTime, AppUser owner) {
        this.name = name;
        this.description = description;
        this.creationTime = creationTime;
        this.owner = owner;
    }

    public AppUser getOwner() {
        return owner;
    }

    public void setOwner(AppUser owner) {
        this.owner = owner;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Notebook) obj;
        return this.id == that.id &&
                Objects.equals(this.name, that.name) &&
                Objects.equals(this.creationTime, that.creationTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, creationTime);
    }

}


