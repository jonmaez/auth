package com.ahsanb.auth.tenant.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@ApiModel(description = "Class representing a LoginResponse")
public class LoginResponse {
	public LoginResponse() {}
	
	public LoginResponse(String accessToken, Long id, String username, String email, List<String> roles) {
		this.token = accessToken;
		this.id = id;
		this.username = username;
		this.email = email;
		this.roles = roles;
	}
	
    @ApiModelProperty(value = "Unique ID of the User")
	@JsonProperty
	private Long id;
    
    @ApiModelProperty(value = "Username of user")
	@JsonProperty
	private String username;
    

    @ApiModelProperty(value = "E-mail of user")
	@JsonProperty
	private String email;
    

    @ApiModelProperty(value = "Set of roles assigned to user")
	@JsonProperty
	private List<String> roles;
    

    @ApiModelProperty(value = "JWT token returned from authentication")
	@JsonProperty
	private String token;
    

    @ApiModelProperty(value = "Token type")
	@JsonProperty
	private String type = "Bearer";
}
