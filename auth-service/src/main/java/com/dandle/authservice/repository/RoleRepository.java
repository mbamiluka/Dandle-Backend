package com.dandle.authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dandle.authservice.model.Role;
import com.dandle.authservice.model.RoleName;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(RoleName roleName);
}