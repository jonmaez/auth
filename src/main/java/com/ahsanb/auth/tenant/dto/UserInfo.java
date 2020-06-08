package com.ahsanb.auth.tenant.dto;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.ahsanb.auth.tenant.entities.enums.RoleType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@ApiModel(description = "Class representing a UserInfo")
public class UserInfo {
	public UserInfo() {}

	public UserInfo(String username, String password, String email) {
		this.username = username;
		this.password = password;
		this.email = email;
	}
	
    @ApiModelProperty(value = "Unique ID of the User")
	@JsonProperty
	private Long id;

	@Valid
    @ApiModelProperty(value = "Unique username for user")
	@JsonProperty
    @NotNull(message = "Username cannot be null")
    @NotBlank(message = "Username cannot be empty")
    @Size(min = 3, max = 20)
	private String username;
   
	@Valid
    @ApiModelProperty(value = "Unique email for user")
	@JsonProperty
    @NotNull(message = "E-mail cannot be null")
    @NotBlank(message = "E-mail cannot be empty")
    @Size(min = 1, max = 50)
	@Email(message = "E-mail must be valid")
	private String email;

	@Valid
    @ApiModelProperty(value = "Set of roles to assign to user")
	@JsonProperty
    @NotNull(message = "Roles cannot be null")
    private Set<RoleType> roles = new HashSet<RoleType>(Arrays.asList(RoleType.ROLE_USER));
    
	@Valid
    @ApiModelProperty(value = "Password for user")
	@JsonProperty(access = Access.WRITE_ONLY)
    @NotNull(message = "Password cannot be null")
    @NotBlank(message = "Password cannot be empty")
    @Size(min = 1, max = 50)
	private String password;
    
	@Valid
    @ApiModelProperty(value = "Date user was added")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") 
    @NotNull
    private Date dateAdded = new Date();
		
    @Override
    public String toString() {
        return "User [id=" + id + ", username=" + username + ", email=" + email + ", roles=" + roles + ", dateAdded=" + dateAdded + "]";
    }
}
