package com.dandle.authservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import com.dandle.authservice.repository.RoleRepository;

import jakarta.annotation.Nonnull;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;
import java.util.Optional;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @Nonnull
    private String authName;

    private RoleRepository roleRepository;

    @Override
    public String getAuthority() {
        return authName;
    }

    public Role getRoleByName(RoleName roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found"));
    }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role)) return false;
        Role that = (Role) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(authName, that.authName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, authName);
    }

    // toString
    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", authName='" + authName + '\'' +
                '}';
    }

}
