package com.ahsanb.auth.t0.services;

import com.ahsanb.auth.t0.entities.T0Tenant;

public interface T0TenantService {

    T0Tenant getByClientId(Integer clientId);
}
