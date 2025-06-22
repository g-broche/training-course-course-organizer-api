package com.gbroche.courseorganizer.dto;

import java.time.LocalDate;
import java.util.Set;

public class PromoRequest {
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long statusId;
    private Set<Long> teamIds;

    public PromoRequest() {
        // name;
        // description;
        // startDate;
        // endDate;
        // statusId;
        // teamIds;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    public LocalDate getStartDate() {
        return this.startDate;
    }

    public void setStartDate(LocalDate value) {
        this.startDate = value;
    }

    public LocalDate getEndDate() {
        return this.endDate;
    }

    public void setEndDate(LocalDate value) {
        this.endDate = value;
    }

    public Long getStatusId() {
        return this.statusId;
    }

    public void setStatus(Long id) {
        this.statusId = id;
    }

    public Set<Long> getTeamIds() {
        return this.teamIds;
    }

    public void setTeamIds(Set<Long> userIds) {
        this.teamIds = userIds;
    }
}
