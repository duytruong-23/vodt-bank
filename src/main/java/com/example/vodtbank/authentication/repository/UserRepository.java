package com.example.vodtbank.authentication.repository;

import java.util.Optional;

import com.example.vodtbank.authentication.entity.User;
import jdk.jfr.Registered;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Registered
public interface UserRepository extends JpaRepository<User, Long> {
	@EntityGraph(attributePaths = { "roles" })
	Optional<User> findByEmail(String email);

	@Query("""
			    SELECT u FROM User u
			    WHERE NOT EXISTS (
			        SELECT r FROM u.roles r WHERE r.name = :roleName
			    )
			""")
	Page<User> findAllExcludeRole(@Param("roleName") String roleName, Pageable pageable);
}
