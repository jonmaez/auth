package com.ahsanb.auth.t0.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ahsanb.auth.t0.dao.T0TenantRepository;
import com.ahsanb.auth.t0.entities.T0Tenant;

@Service("t0TenantService")
public class T0TenantServiceImpl implements T0TenantService{
	
    @Autowired
    T0TenantRepository masterTenantRepository;

    @Override
    public T0Tenant getByClientId(Integer tenantId) {
        return masterTenantRepository.findByTenantId(tenantId);
    }
}
