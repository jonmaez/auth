package com.ahsanb.auth.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.ahsanb.auth.tenant.dto.LoginRequest;
import com.ahsanb.auth.tenant.dto.LoginResponse;
import com.ahsanb.auth.tenant.entities.UserDetailsImpl;
import com.ahsanb.auth.tenant.exceptions.InvalidCredentialsException;
import com.ahsanb.auth.tenant.exceptions.RoleNotFoundException;
import com.ahsanb.auth.tenant.exceptions.UserException;
import com.ahsanb.auth.tenant.services.AuthService;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AuthServiceTest {
	
    @Autowired
    private AuthService authService;
    
    @MockBean
    private UserDetailsImpl applicationUser;
    
    @MockBean
    private Authentication authentication;

    @MockBean
    private SecurityContext securityContext;
    
    @MockBean
	private AuthenticationManager authenticationManager;    
   
    @Test
    public void givenValidCredentials_whenSignIn_thenUserIsLoggedIn() throws RoleNotFoundException, UserException, InvalidCredentialsException {
    	LoginRequest loginRequest = new LoginRequest("user", "user123");    	
        
        when(authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()))).thenReturn(authentication);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(applicationUser);
        
        LoginResponse loginResponse = authService.authenticate(loginRequest);
        
        verify(authenticationManager, times(1)).authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        verify(securityContext, times(1)).getAuthentication();
        verifyNoMoreInteractions(authenticationManager);
        
        assertNotNull(loginResponse.getToken());
        assertFalse(loginResponse.getToken().isEmpty()); 
    }
    
    @Test
    public void givenInvalidCredentials_whenSignIn_thenUserIsNotLoggedIn() {
    	LoginRequest loginRequest = new LoginRequest("user", "user123");    	
        
        when(authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()))).thenThrow(BadCredentialsException.class);
       
        Exception exception = assertThrows(InvalidCredentialsException.class, () -> {
        	authService.authenticate(loginRequest);
        });        

        verify(authenticationManager, times(1)).authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        verifyNoMoreInteractions(authenticationManager);
        
        String expectedMessage = String.format("Invalid credentials!");
    	String actualMessage = exception.getMessage();
    	
    	assertEquals(actualMessage, expectedMessage);
    }
}
