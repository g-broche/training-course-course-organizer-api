package com.gbroche.courseorganizer.interfaces;

import com.gbroche.courseorganizer.enums.RecordStatus;

public interface RecordStatusTrackable {
    RecordStatus getRecordStatus();

    void setRecordStatus(RecordStatus status);
}
