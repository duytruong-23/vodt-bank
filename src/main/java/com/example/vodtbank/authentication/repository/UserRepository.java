package com.example.vodtbank.authentication.repository;

import java.util.Optional;

import com.example.vodtbank.authentication.entity.User;
import jdk.jfr.Registered;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

@Registered
public interface UserRepository extends JpaRepository<User, Long> {
	@EntityGraph(attributePaths = { "roles" })
	Optional<User> findByEmail(String email);
}
