package com.ahsanb.auth.dto;

import java.util.List;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@ApiModel(description = "Class representing a LoginResponse")
public class LoginResponse {
	private Long id;
	private String username;
	private String email;
	private List<String> roles;
	private String token;
	private String type = "Bearer";
	
	public LoginResponse(String accessToken, Long id, String username, String email, List<String> roles) {
		this.token = accessToken;
		this.id = id;
		this.username = username;
		this.email = email;
		this.roles = roles;
	}
}
