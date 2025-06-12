package com.gbroche.courseorganizer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gbroche.courseorganizer.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

}
