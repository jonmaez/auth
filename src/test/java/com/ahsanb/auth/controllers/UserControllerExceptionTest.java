package com.ahsanb.auth.controllers;

import static org.hamcrest.CoreMatchers.is;
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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.ahsanb.auth.security.UserDetailsServiceImpl;
import com.ahsanb.auth.tenant.dto.UserInfo;
import com.ahsanb.auth.tenant.exceptions.UserException;
import com.ahsanb.auth.tenant.exceptions.UserNotFoundException;
import com.ahsanb.auth.tenant.services.UserService;
import com.ahsanb.auth.util.JsonUtil;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
public class UserControllerExceptionTest {
	
	private static final String ROOT_URI = "/users";
	private static final Long VALID_USER_ID = Long.valueOf(11);
	private static final Long INVALID_USER_ID = Long.valueOf(-99);
	
    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;
    
    @MockBean
	UserDetailsServiceImpl userDetailsService;

    @Test
    public void testUserNotFoundExceptionOnGet() throws Exception {
        given(userService.getUserInfoById(INVALID_USER_ID))
        						   .willThrow(new UserNotFoundException(INVALID_USER_ID));
        
        mvc.perform(get(ROOT_URI + "/" + INVALID_USER_ID)
           .contentType(MediaType.APPLICATION_JSON))
           .andExpect(status().isNotFound())
           .andExpect(jsonPath("$.message", is(String.format("User not found with id: [%s]", INVALID_USER_ID))));

        verify(userService, times(1)).getUserInfoById(INVALID_USER_ID);
        verifyNoMoreInteractions(userService);
    }
    
    @Test
    public void testMethodArgumentTypeMismatchExceptionOnGet() throws Exception {       
        mvc.perform(get(ROOT_URI + "/null")
           .contentType(MediaType.APPLICATION_JSON))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.message", is("Invalid request")));

        verifyNoMoreInteractions(userService);
    }
    
    @Test
    public void testMethodArgumentNotValidExceptionOnAdd() throws Exception {
    	UserInfo cheerios = new UserInfo("user", "user123", "INVALID");

        mvc.perform(post(ROOT_URI)
           .contentType(MediaType.APPLICATION_JSON)
           .content(JsonUtil.asJsonString(cheerios)))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.message", is("Invalid request")));

        verifyNoMoreInteractions(userService);
    }
    
    @Test
    public void testHttpMessageNotReadableExceptionOnAdd() throws Exception {       
        mvc.perform(post(ROOT_URI)
           .contentType(MediaType.APPLICATION_JSON))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.message", is("Invalid request")));

        verifyNoMoreInteractions(userService);
    }
    
    @Test
    public void testUserExceptionOnAdd() throws Exception {
    	UserInfo user = new UserInfo("user", "user123", "user@ahsanb.com");
        given(userService.addUserInfo(any(UserInfo.class)))
        						   .willThrow(new UserException(String.format("E-mail [%s] already in use", user.getEmail())));
        
        // Not deserializing from user object because "password" attribute is ignored
        String payload = "{\"username\":\"user\",\"email\":\"user@ahsanb.com\",\"roles\":[\"user\"],\"password\":\"user123\"}";
        
        mvc.perform(post(ROOT_URI)
           .contentType(MediaType.APPLICATION_JSON)
           .content(payload))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.message", is(String.format("E-mail [%s] already in use", user.getEmail()))));

        verify(userService, times(1)).addUserInfo(any(UserInfo.class));
        verifyNoMoreInteractions(userService);
    }
    
    @Test
    public void testMethodArgumentNotValidExceptionOnUpdate() throws Exception {
    	UserInfo existing = new UserInfo("user", "user123", "user@ahsanb.com");
    	existing.setId(VALID_USER_ID);
    	
    	UserInfo toUpdate = new UserInfo("user", "user123", "INVALID");
    	
        mvc.perform(post(ROOT_URI + "/" + existing.getId())
           .contentType(MediaType.APPLICATION_JSON)
           .content(JsonUtil.asJsonString(toUpdate)))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.message", is("Invalid request")));

        verifyNoMoreInteractions(userService);
    }
    
    @Test
    public void testHttpMessageNotReadableExceptionOnUpdate() throws Exception {
    	UserInfo existing = new UserInfo("user", "user123", "user@ahsanb.com");
    	existing.setId(VALID_USER_ID);
    	    	
        mvc.perform(post(ROOT_URI + "/" + existing.getId())
           .contentType(MediaType.APPLICATION_JSON))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.message", is("Invalid request")));

        verifyNoMoreInteractions(userService);
    }
    
    @Test
    public void testMethodArgumentTypeMismatchExceptionOnUpdate() throws Exception {
    	UserInfo toUpdate = new UserInfo("user", "user123", "user@ahsanb.com");
                   
        mvc.perform(post(ROOT_URI + "/null")
           .contentType(MediaType.APPLICATION_JSON)
           .content(JsonUtil.asJsonString(toUpdate)))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.message", is("Invalid request")));

        verifyNoMoreInteractions(userService);
    }
    
    @Test
    public void testUserExceptionOnUpdate() throws Exception {
    	UserInfo existing = new UserInfo("user", "user123", "user@ahsanb.com");
    	existing.setId(VALID_USER_ID);
    	   	
        given(userService.updateUserInfo(any(UserInfo.class), any(Long.class)))
        						   .willThrow(new UserException(String.format("E-mail [%s] already in use", existing.getEmail())));
                   
        // Not deserializing from object because "password" attribute is ignored
        String payload = "{\"username\":\"user\",\"email\":\"user@ahsanb.com\",\"roles\":[\"user\"],\"password\":\"3456\"}";
        
        mvc.perform(post(ROOT_URI + "/" + existing.getId())
           .contentType(MediaType.APPLICATION_JSON)
           .content(payload))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.message", is(String.format("E-mail [%s] already in use", existing.getEmail()))));

        verify(userService, times(1)).updateUserInfo(any(UserInfo.class), any(Long.class));
        verifyNoMoreInteractions(userService);
    }
    
    @Test
    public void testUserNotFoundExceptionOnUpdate() throws Exception {  	
        given(userService.updateUserInfo(any(UserInfo.class), any(Long.class)))
        						   .willThrow(new UserNotFoundException(INVALID_USER_ID));
        
        // Not deserializing from newUser object because "password" attribute is ignored
        String payload = "{\"username\":\"user\",\"email\":\"user@ahsanb.com\",\"roles\":[\"user\"],\"password\":\"user123\"}";
                   
        mvc.perform(post(ROOT_URI + "/" + INVALID_USER_ID)
           .contentType(MediaType.APPLICATION_JSON)
           .content(payload))
           .andExpect(status().isNotFound())
           .andExpect(jsonPath("$.message", is(String.format("User not found with id: [%s]", INVALID_USER_ID))));

        verify(userService, times(1)).updateUserInfo(any(UserInfo.class), any(Long.class));
        verifyNoMoreInteractions(userService);
    }
    
    @Test
    public void testMethodArgumentTypeMismatchExceptionOnDelete() throws Exception {                   
        mvc.perform(delete(ROOT_URI + "/null")
           .contentType(MediaType.APPLICATION_JSON))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.message", is("Invalid request")));

        verifyNoMoreInteractions(userService);
    }
}
