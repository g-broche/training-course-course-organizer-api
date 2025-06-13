package com.gbroche.courseorganizer.model;

import com.gbroche.courseorganizer.enums.RecordStatus;
import com.gbroche.courseorganizer.interfaces.RecordStatusTrackable;

import jakarta.persistence.*;

@MappedSuperclass
public abstract class RecordStatusEntity implements RecordStatusTrackable {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecordStatus recordStatus = RecordStatus.SHOWN;

    @Override
    public RecordStatus getRecordStatus() {
        return recordStatus;
    }

    @Override
    public void setRecordStatus(RecordStatus status) {
        this.recordStatus = status;
    }
}