package com.ahsanb.auth.tenant.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.ahsanb.auth.tenant.entities.Role;
import com.ahsanb.auth.tenant.entities.enums.RoleType;

@RepositoryRestResource
public interface RoleRepository extends JpaRepository<Role, Long> {
	Optional<Role> findByName(RoleType name);
}