package com.gbroche.courseorganizer.dto;

import com.gbroche.courseorganizer.model.Brief;

import java.time.LocalDateTime;

public class BriefDTO {
    private Long id;
    private String name;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime editedAt;
    private String status;
    private UserDTO author;

    public BriefDTO(Brief brief) {
        this.id = brief.getId();
        this.name = brief.getName();
        this.content = brief.getContent();
        this.createdAt = brief.getCreatedAt();
        this.editedAt = brief.getEditedAt();
        this.status = brief.getStatus().getLabel();
        this.author = new UserDTO(brief.getAuthor());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getEditedAt() {
        return editedAt;
    }

    public void setEditedAt(LocalDateTime editedAt) {
        this.editedAt = editedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UserDTO getAuthor() {
        return author;
    }

    public void setAuthor(UserDTO author) {
        this.author = author;
    }
}
