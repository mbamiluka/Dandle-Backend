package com.dandle.authservice.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.dandle.authservice.model.User;

import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, Long> {
    User findByEmail(String email);

    boolean existsByEmail(Object email);

    User save(User user);
}


