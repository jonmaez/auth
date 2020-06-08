package com.ahsanb.auth.tenant.services;

import com.ahsanb.auth.tenant.dto.ListUserResponse;
import com.ahsanb.auth.tenant.dto.UserInfo;
import com.ahsanb.auth.tenant.exceptions.RoleNotFoundException;
import com.ahsanb.auth.tenant.exceptions.UserException;
import com.ahsanb.auth.tenant.exceptions.UserNotFoundException;

public interface UserService {

	UserInfo getUserInfoById(Long id) throws UserNotFoundException;
	
	ListUserResponse getAllUserInfos();
	
	UserInfo addUserInfo(UserInfo userInfo) throws UserException, RoleNotFoundException;
		
	UserInfo updateUserInfo(UserInfo user, Long id) throws UserException;
	
	void deleteUser(Long id);
}
