package com.ahsanb.auth.controllers;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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

import com.ahsanb.auth.dto.LoginRequest;
import com.ahsanb.auth.exceptions.InvalidCredentialsException;
import com.ahsanb.auth.security.services.UserDetailsServiceImpl;
import com.ahsanb.auth.services.AuthService;
import com.ahsanb.auth.util.JsonUtil;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AuthController.class)
public class AuthControllerExceptionTest {
	
	private static final String ROOT_URI = "/auth";
	
    @Autowired
    private MockMvc mvc;

    @MockBean
    private AuthService authService;
    
    @MockBean
	UserDetailsServiceImpl userDetailsService;
    
    @Test
    public void testUserCredentialsExceptionOnGet() throws Exception {
    	LoginRequest loginRequest = new LoginRequest(0, "user", "user123");
        given(authService.authenticate(loginRequest))
		   .willThrow(new InvalidCredentialsException("Invalid Credentials!"));
        
        mvc.perform(post(ROOT_URI + "/signin")
           .contentType(MediaType.APPLICATION_JSON)
           .content(JsonUtil.asJsonString(loginRequest)))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.message", is("Invalid Credentials!")));
        
        verify(authService, times(1)).authenticate(loginRequest);
        verifyNoMoreInteractions(authService);
    }

}
