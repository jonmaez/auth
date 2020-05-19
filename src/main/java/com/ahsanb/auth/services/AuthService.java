package com.ahsanb.auth.services;

import com.ahsanb.auth.dto.LoginRequest;
import com.ahsanb.auth.dto.LoginResponse;
import com.ahsanb.auth.exceptions.InvalidCredentialsException;

public interface AuthService {
	
	LoginResponse authenticate(LoginRequest loginRequest) throws InvalidCredentialsException;
}
