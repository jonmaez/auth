package com.ahsanb.auth.controllers;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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
import com.ahsanb.auth.tenant.dto.LoginRequest;
import com.ahsanb.auth.tenant.dto.LoginResponse;
import com.ahsanb.auth.tenant.services.AuthService;
import com.ahsanb.auth.util.JsonUtil;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AuthController.class)
public class AuthControllerTest {
	
	private static final String ROOT_URI = "/auth";
	
    @Autowired
    private MockMvc mvc;

    @MockBean
    private AuthService authService;
    
    @MockBean
	UserDetailsServiceImpl userDetailsService;
    
    @Test
    public void whenValidLoginRequest_thenSuccessfulLogIn() throws Exception {
    	LoginRequest loginRequest = new LoginRequest("master", "user", "user123");
    	LoginResponse loginResponse = new LoginResponse("fake_token", 1L, "user", "user@ahsanb.com", Arrays.asList("user"));
        given(authService.authenticate(loginRequest)).willReturn(loginResponse);
        
        mvc.perform(post(ROOT_URI + "/signin")
           .contentType(MediaType.APPLICATION_JSON)
           .content(JsonUtil.asJsonString(loginRequest)))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.username", is(loginRequest.getUsername())));
        
        verify(authService, times(1)).authenticate(loginRequest);
        verifyNoMoreInteractions(authService);
    }
}
