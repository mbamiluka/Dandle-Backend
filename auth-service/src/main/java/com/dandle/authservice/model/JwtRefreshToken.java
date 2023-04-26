package com.dandle.authservice.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "jwt_refresh_tokens")
public class JwtRefreshToken implements Serializable {
    private static final long serialVersionUID = -8091879091924046844L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private LocalDateTime createdDate;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    public JwtRefreshToken() {}

    public JwtRefreshToken(String token, LocalDateTime createdDate, LocalDateTime expiryDate) {
        this.token = token;
        this.createdDate = createdDate;
        this.expiryDate = expiryDate;
    }

    // getters and setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String refreshToken) {
        this.token = refreshToken;
    }
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }
    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JwtRefreshToken)) return false;
        JwtRefreshToken that = (JwtRefreshToken) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getToken(), that.getToken()) &&
                Objects.equals(getCreatedDate(), that.getCreatedDate()) &&
                Objects.equals(getExpiryDate(), that.getExpiryDate());
    }
}
