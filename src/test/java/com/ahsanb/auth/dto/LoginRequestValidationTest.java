package com.ahsanb.auth.dto;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class LoginRequestValidationTest {
	
    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    
    @Test
    public void testValidLoginRequest() {
        LoginRequest loginRequest = new LoginRequest(0, "admin", "admin123");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
        assertTrue(violations.isEmpty());
    }
    
    @Test
    public void testEmptyUsername() {
        LoginRequest loginRequest = new LoginRequest(0, "", "admin123");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
        assertFalse(violations.isEmpty());
        assertTrue(violations.toString().contains("size must be between 3 and 20"));
    }

    @Test
    public void testNullUsername() {
        LoginRequest loginRequest = new LoginRequest(0, null, "admin123");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
        assertFalse(violations.isEmpty());
        assertTrue(violations.toString().contains("Username cannot be null"));
    }
    
    @Test
    public void testVeryLongUsername() {
        LoginRequest loginRequest = new LoginRequest(0, "INVALIDINVALIDINVALIDINVALIDINVALID", "admin123");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
        assertFalse(violations.isEmpty());
        assertTrue(violations.toString().contains("size must be between 3 and 20"));
    }
    
    @Test
    public void testEmptyPassword() {
        LoginRequest loginRequest = new LoginRequest(0, "admin", "");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
        assertFalse(violations.isEmpty());
        assertTrue(violations.toString().contains("Password cannot be empty"));
    }

    @Test
    public void testNullPassword() {
        LoginRequest loginRequest = new LoginRequest(0, "admin", null);
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
        assertFalse(violations.isEmpty());
        assertTrue(violations.toString().contains("Password cannot be null"));
    }
    
    @Test
    public void testVeryLongPassword() {
        LoginRequest loginRequest = new LoginRequest(0, "admin", "INVALIDINVALIDINVALIDINVALIDINVALIDINVALIDINVALIDINVALID");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);
        assertFalse(violations.isEmpty());
        assertTrue(violations.toString().contains("size must be between 1 and 50"));
    }
}
