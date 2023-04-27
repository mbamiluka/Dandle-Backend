package com.dandle.authservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dandle.authservice.model.Role;
import com.dandle.authservice.model.RoleName;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName roleName);

    Optional<Role> findRoleByAuthName(String roleName);
}