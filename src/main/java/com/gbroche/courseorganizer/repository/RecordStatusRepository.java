package com.gbroche.courseorganizer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.gbroche.courseorganizer.enums.RecordStatus;
import com.gbroche.courseorganizer.interfaces.RecordStatusTrackable;

@NoRepositoryBean
public interface RecordStatusRepository<T extends RecordStatusTrackable, ID>
        extends JpaRepository<T, ID> {

    List<T> findByRecordStatus(RecordStatus status);

    List<T> findByRecordStatusNot(RecordStatus status);

    List<T> findByRecordStatusNotIn(RecordStatus[] statuses);
}
