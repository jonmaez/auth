package com.ahsanb.auth.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ahsanb.auth.dto.ListUserResponse;
import com.ahsanb.auth.dto.UserInfo;
import com.ahsanb.auth.exceptions.RoleNotFoundException;
import com.ahsanb.auth.exceptions.UserException;
import com.ahsanb.auth.exceptions.UserNotFoundException;
import com.ahsanb.auth.services.UserService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/users")
public class UserController {
	
	@Autowired
	UserService userService;

	@PreAuthorize("hasAuthority('admin')")
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Create a user", response = UserInfo.class)
	public @ResponseBody UserInfo addUser(@Valid @RequestBody final UserInfo userInfo) throws RoleNotFoundException, UserException {
		return userService.addUserInfo(userInfo);
	}
    
	@PreAuthorize("hasAuthority('admin')")
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "List All Users", response = ListUserResponse.class)
	public @ResponseBody ListUserResponse getUsers() {
		return userService.getAllUserInfos();
	}
    
	@PreAuthorize("hasAuthority('admin')")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Describe a User", response = UserInfo.class)
    public @ResponseBody UserInfo getUser(@PathVariable final Long id) throws UserNotFoundException {
        return userService.getUserInfoById(id);
    }
    
	@PreAuthorize("hasAuthority('admin')")
    @PostMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Update a User by ID", response = UserInfo.class)
    public @ResponseBody UserInfo updateDepartment(
    		@Valid @RequestBody final UserInfo userInfo, @PathVariable final Long id) throws UserException {
        return userService.updateUserInfo(userInfo, id);
    }
    
	@PreAuthorize("hasAuthority('admin')")
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Delete an User by ID", response = UserInfo.class)
    public @ResponseBody ResponseEntity<String> deleteUser( @PathVariable final Long id) {
    	userService.deleteUser(id);
        return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
    }
}
