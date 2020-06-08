package com.ahsanb.auth.tn.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.ahsanb.auth.tn.entities.User;

@RepositoryRestResource
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUsername(String username);

	Optional<User> findByEmail(String email);
	
	Boolean existsByUsername(String username);

	Boolean existsByEmail(String email);
}
