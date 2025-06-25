package com.gbroche.courseorganizer.dto;

import com.gbroche.courseorganizer.model.Brief;

import java.time.LocalDateTime;

public class BriefRequest {
    private String name;
    private String content;
    private Long statusId;

    public BriefRequest(String name, String content, Long statusId) {
        this.name = name;
        this.content = content;
        this.statusId = statusId;
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
}
