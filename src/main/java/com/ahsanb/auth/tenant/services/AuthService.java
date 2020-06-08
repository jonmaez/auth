package com.ahsanb.auth.tenant.services;

import com.ahsanb.auth.tenant.dto.LoginRequest;
import com.ahsanb.auth.tenant.dto.LoginResponse;
import com.ahsanb.auth.tenant.exceptions.InvalidCredentialsException;

public interface AuthService {
	
	LoginResponse authenticate(LoginRequest loginRequest) throws InvalidCredentialsException;
}
