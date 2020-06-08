package com.ahsanb.auth.controllers;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.ahsanb.auth.security.UserDetailsServiceImpl;
import com.ahsanb.auth.tenant.dto.ListUserResponse;
import com.ahsanb.auth.tenant.dto.UserInfo;
import com.ahsanb.auth.tenant.services.UserService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {
	
	private static final String ROOT_URI = "/users";
	private static final Long VALID_USER_ID = Long.valueOf(11);
	
    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;
    
    @MockBean
	UserDetailsServiceImpl userDetailsService;
    
    @Test
    public void whenValidAddUser_thenCreateUser() throws Exception {
    	UserInfo newUser = new UserInfo("user", "user123", "user@ahsanb.com");
        given(userService.addUserInfo(any(UserInfo.class))).willReturn(newUser);
        
        // Not deserializing from newUser object because "password" attribute is ignored
        String payload = "{\"username\":\"user\",\"email\":\"user@ahsanb.com\",\"roles\":[\"user\"],\"password\":\"user123\"}";
        
        mvc.perform(post(ROOT_URI)
           .contentType(MediaType.APPLICATION_JSON)
           .content(payload))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.username", is(newUser.getUsername())));
        
        verify(userService, times(1)).addUserInfo(any(UserInfo.class));
        verifyNoMoreInteractions(userService);
    }
    
    @Test
    public void whenValidUser_thenUpdateUser() throws Exception {
    	UserInfo existing = new UserInfo("user", "user123", "user@ahsanb.com");
    	existing.setId(VALID_USER_ID);
    	
    	UserInfo toUpdate = new UserInfo("user", "user123", "user123@ahsanb.com");
        given(userService.updateUserInfo(any(UserInfo.class), any(Long.class))).willReturn(toUpdate);
        
        // Not deserializing from toUpdate object because "password" attribute is ignored
        String payload = "{\"username\":\"user\",\"email\":\"user123@ahsanb.com\",\"roles\":[\"user\"],\"password\":\"user123\"}";

        mvc.perform(post(ROOT_URI + "/" + existing.getId())
           .contentType(MediaType.APPLICATION_JSON)
           .content(payload))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.email", is(toUpdate.getEmail())));
        
        verify(userService, times(1)).updateUserInfo(any(UserInfo.class), any(Long.class));
        verifyNoMoreInteractions(userService);
    }
    
    @Test
    public void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
        UserInfo user1 = new UserInfo("user1", "user1", "user1@ahsanb.com");
        UserInfo user2 = new UserInfo("user2", "user2", "user2@ahsanb.com");
        UserInfo user3 = new UserInfo("user3", "user3", "user3@ahsanb.com");
    	ListUserResponse allUsers = new ListUserResponse(Arrays.asList(user1, user2, user3));

        given(userService.getAllUserInfos()).willReturn(allUsers);

        mvc.perform(get(ROOT_URI)
           .contentType(MediaType.APPLICATION_JSON))
	       .andExpect(status().isOk())
	       .andExpect(jsonPath("$.users", hasSize(3)))
	       .andExpect(jsonPath("$.users[0].username", is(user1.getUsername())))
	       .andExpect(jsonPath("$.users[1].username", is(user2.getUsername())))
	       .andExpect(jsonPath("$.users[2].username", is(user3.getUsername())));
        
        verify(userService, times(1)).getAllUserInfos();
        verifyNoMoreInteractions(userService);
    }
    
    @Test
    public void whenExistingUser_thenGetUser() throws Exception {
        UserInfo existing = new UserInfo("user", "user", "user@ahsanb.com");
    	existing.setId(VALID_USER_ID);
    	
        given(userService.getUserInfoById((existing.getId()))).willReturn(existing);       

        mvc.perform(get(ROOT_URI + "/" + existing.getId())
           .contentType(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.username", is(existing.getUsername())));
        
        verify(userService, times(1)).getUserInfoById(existing.getId());
        verifyNoMoreInteractions(userService);
    }
    
    @Test
    public void whenExistingUser_thenDeleteUser() throws Exception {
    	UserInfo existing = new UserInfo("user", "user", "user@ahsanb.com");
        existing.setId(VALID_USER_ID);
        
        mvc.perform(delete(ROOT_URI + "/" + existing.getId())
           .contentType(MediaType.APPLICATION_JSON))
           .andExpect(status().is(204))
           .andExpect(jsonPath("$").doesNotExist());
        
        verify(userService, times(1)).deleteUser(existing.getId());
        verifyNoMoreInteractions(userService);
    }
}
