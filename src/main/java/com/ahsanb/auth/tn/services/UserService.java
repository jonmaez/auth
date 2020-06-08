package com.ahsanb.auth.tn.services;

import com.ahsanb.auth.exceptions.RoleNotFoundException;
import com.ahsanb.auth.exceptions.UserException;
import com.ahsanb.auth.exceptions.UserNotFoundException;
import com.ahsanb.auth.tn.dto.ListUserResponse;
import com.ahsanb.auth.tn.dto.UserInfo;

public interface UserService {

	UserInfo getUserInfoById(Long id) throws UserNotFoundException;
	
	ListUserResponse getAllUserInfos();
	
	UserInfo addUserInfo(UserInfo userInfo) throws UserException, RoleNotFoundException;
		
	UserInfo updateUserInfo(UserInfo user, Long id) throws UserException;
	
	void deleteUser(Long id);
}
