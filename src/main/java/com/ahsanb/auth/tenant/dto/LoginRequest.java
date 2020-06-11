package com.ahsanb.auth.tenant.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@ApiModel(description = "Class representing a LoginRequest")
public class LoginRequest {
	
	public LoginRequest() {}
	
	@Valid
    @ApiModelProperty(value = "Account name")
	@JsonProperty("account")
    @NotNull(message = "Account name cannot be null")
    @NotBlank(message = "Account name cannot be empty")
    @Size(min = 3, max = 20)
	private String tenantId;
	
	@Valid
    @ApiModelProperty(value = "Username of user")
	@JsonProperty
    @NotNull(message = "Username cannot be null")
    @NotBlank(message = "Username cannot be empty")
    @Size(min = 3, max = 20)
	private String username;

	@Valid
    @ApiModelProperty(value = "Password for user")
	@JsonProperty
    @NotNull(message = "Password cannot be null")
    @NotBlank(message = "Password cannot be empty")
    @Size(min = 1, max = 50)
	private String password;
}
