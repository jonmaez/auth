package com.ahsanb.auth.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ahsanb.auth.dao.RoleRepository;
import com.ahsanb.auth.dao.UserRepository;
import com.ahsanb.auth.dto.ListUserResponse;
import com.ahsanb.auth.dto.UserInfo;
import com.ahsanb.auth.entities.Role;
import com.ahsanb.auth.entities.User;
import com.ahsanb.auth.entities.enums.RoleType;
import com.ahsanb.auth.exceptions.RoleNotFoundException;
import com.ahsanb.auth.exceptions.UserException;
import com.ahsanb.auth.exceptions.UserNotFoundException;

@Service("userService")
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;
	
    @Autowired
    @Qualifier("UserRetriever")
    private ModelMapper userRetriever;
    
	@Override
	public UserInfo addUserInfo(UserInfo userInfo) throws UserException, RoleNotFoundException {
		userInfo.setId(null);
		if (userRepository.existsByUsername(userInfo.getUsername())) {
	            throw new UserException(String.format("Username [%s] already in use", userInfo.getUsername()));
			}
		
		if (userRepository.existsByEmail(userInfo.getEmail())) {
            throw new UserException(String.format("Email [%s] already in use", userInfo.getEmail()));
		}

		// username can only be set on creation of user. Cannot be modified later.
		User newUser = new User();
		newUser.setUsername(userInfo.getUsername());
		
		return saveUserInfo(userInfo, newUser);
	}
		
    private UserInfo saveUserInfo(UserInfo userInfo, User user) throws UserException {
    	user.setId(userInfo.getId());
    	user.setPassword(encoder.encode(userInfo.getPassword()));
    	user.setEmail(userInfo.getEmail());
    	
    	Set<RoleType> roleTypes = userInfo.getRoles();
    	
    	if(roleTypes == null) {
    		throw new UserException("Valid role(s) must be specified");
    	}
    	
		Set<Role> roles = new HashSet<>(); 

		for(RoleType role : roleTypes) {
	    	if(role == null) {
	    		throw new UserException("Valid role(s) must be specified");
	    	}
			Role roleFound = roleRepository.findByName(role)
										   .orElseThrow(() -> new RoleNotFoundException(role.getRoleName()));
			roles.add(roleFound);			
		}
		
		user.setRoles(roles);
		userRepository.save(user);

		return userRetriever.map(user, UserInfo.class);
    }

	@Override
	public UserInfo getUserInfoById(Long id) throws UserNotFoundException {
		User user = userRepository.findById(id).orElse(null);
        if (user == null) {
        	throw new UserNotFoundException(id);
        }  
        return userRetriever.map(user, UserInfo.class);
	}

	@Override
	public ListUserResponse getAllUserInfos() {
		List<User> users = userRepository.findAll();	
        List<UserInfo> userInfos = users.stream().
        					map(user -> userRetriever.map(user, UserInfo.class))
        					.collect(Collectors.toList());
        
		return new ListUserResponse(userInfos);
	}

	@Override
	public UserInfo updateUserInfo(UserInfo userInfo, Long id) throws UserException {
		userInfo.setId(id);
		User user = userRepository.findById(id).orElse(null);
        if (user == null) {
        	throw new UserNotFoundException(id);
        }
               
        User foundByEmail = userRepository.findByEmail(userInfo.getEmail()).orElse(null);
        
        if(foundByEmail != null && !foundByEmail.getId().equals(user.getId())) {
            throw new UserException(String.format("E-mail [%s] already in use", userInfo.getEmail()));
        }
        
        return saveUserInfo(userInfo, user);
	}

	@Override
	public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
        	return;
        }
        userRepository.delete(user);			
	}
}
