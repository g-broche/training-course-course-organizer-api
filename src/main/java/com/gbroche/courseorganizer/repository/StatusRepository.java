package com.gbroche.courseorganizer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gbroche.courseorganizer.model.Status;

public interface StatusRepository extends JpaRepository<Status, Long> {

}
