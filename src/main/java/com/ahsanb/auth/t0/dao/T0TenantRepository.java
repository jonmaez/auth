package com.ahsanb.auth.t0.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ahsanb.auth.t0.entities.T0Tenant;

@Repository
public interface T0TenantRepository extends JpaRepository<T0Tenant, Integer> {
    T0Tenant findByTenantId(Integer tenantId);
}
