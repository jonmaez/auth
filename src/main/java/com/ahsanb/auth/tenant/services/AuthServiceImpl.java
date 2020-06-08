package com.ahsanb.auth.tenant.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.ahsanb.auth.tenant.dto.LoginRequest;
import com.ahsanb.auth.tenant.dto.LoginResponse;
import com.ahsanb.auth.tenant.entities.UserDetailsImpl;
import com.ahsanb.auth.tenant.exceptions.InvalidCredentialsException;
import com.ahsanb.auth.util.JwtUtils;

@Service("authService")
public class AuthServiceImpl implements AuthService {
	
	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtils jwtUtils;
	
	@Override
	public LoginResponse authenticate(LoginRequest loginRequest) throws InvalidCredentialsException {
		Authentication authentication = null;
		try {
			authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())); 
		} catch(BadCredentialsException e) {
			throw new InvalidCredentialsException("Invalid credentials!");
		}

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();		
		List<String> roles = userDetails.getAuthorities().stream()
														 .map(item -> item.getAuthority())
														 .collect(Collectors.toList());

		return new LoginResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles);
	}
    
}