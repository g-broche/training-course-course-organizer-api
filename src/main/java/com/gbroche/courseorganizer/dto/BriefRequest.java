package com.gbroche.courseorganizer.dto;

import com.gbroche.courseorganizer.model.Brief;

import java.time.LocalDateTime;

public class BriefRequest {
    private String name;
    private String content;
    private Long statusId;
    private Long authorId;

    public BriefRequest(String name, String content, Long statusId, Long authorId) {
        this.name = name;
        this.content = content;
        this.statusId = statusId;
        this.authorId = authorId;
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

    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }
}
