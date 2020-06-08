package com.ahsanb.auth.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ahsanb.auth.tenant.dto.LoginRequest;
import com.ahsanb.auth.tenant.dto.LoginResponse;
import com.ahsanb.auth.tenant.exceptions.InvalidCredentialsException;
import com.ahsanb.auth.tenant.services.AuthService;

import io.swagger.annotations.ApiOperation;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class AuthController {
	
	@Autowired
	AuthService authService;

    @PostMapping(value = "/signin", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Authenticate a user", response = LoginResponse.class)
	public @ResponseBody LoginResponse authenticateUser(@Valid @RequestBody LoginRequest loginRequest) throws InvalidCredentialsException {
		return authService.authenticate(loginRequest);
	}
}
