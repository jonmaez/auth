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

public class UserInfoValidationTest {
	
    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    
    @Test
    public void testValidUser() {
        UserInfo user = new UserInfo("admin", "admin123", "admin@ahsanb.com");
        Set<ConstraintViolation<UserInfo>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }
    
    @Test
    public void testEmptyUsername() {
        UserInfo user = new UserInfo("", "admin123", "admin@ahsanb.com");
        Set<ConstraintViolation<UserInfo>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.toString().contains("size must be between 3 and 20"));
    }

    @Test
    public void testNullUsername() {
        UserInfo user = new UserInfo(null, "admin123", "admin@ahsanb.com");
        Set<ConstraintViolation<UserInfo>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.toString().contains("Username cannot be null"));
    }
    
    @Test
    public void testVeryLongUsername() {
        UserInfo user = new UserInfo("INVALIDINVALIDINVALIDINVALIDINVALID", "admin123", "admin@ahsanb.com");
        Set<ConstraintViolation<UserInfo>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.toString().contains("size must be between 3 and 20"));
    }
    
    @Test
    public void testInvalidEmail() {
        UserInfo user = new UserInfo("admin", "admin123", "INVALID");
        Set<ConstraintViolation<UserInfo>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.toString().contains("E-mail must be valid"));
    }
    
    @Test
    public void testEmptyEmail() {
        UserInfo user = new UserInfo("admin", "admin123", "");
        Set<ConstraintViolation<UserInfo>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.toString().contains("size must be between 1 and 50"));
    }

    @Test
    public void testNullEmail() {
        UserInfo user = new UserInfo("admin", "admin123", null);
        Set<ConstraintViolation<UserInfo>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.toString().contains("E-mail cannot be null"));
    }
    
    @Test
    public void testVeryLongEmail() {
        UserInfo user = new UserInfo("admin", "admin123", "adminadminadminadminadminadminadminadminadminadmin@ahsanb.com");
        Set<ConstraintViolation<UserInfo>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.toString().contains("size must be between 1 and 50"));
    }
    
    @Test
    public void testEmptyPassword() {
        UserInfo user = new UserInfo("admin", "", "admin@ahsanb.com");
        Set<ConstraintViolation<UserInfo>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.toString().contains("Password cannot be empty"));
    }

    @Test
    public void testNullPassword() {
        UserInfo user = new UserInfo("admin", null, "admin@ahsanb.com");
        Set<ConstraintViolation<UserInfo>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.toString().contains("Password cannot be null"));
    }
    
    @Test
    public void testVeryLongPassword() {
        UserInfo user = new UserInfo("admin", "INVALIDINVALIDINVALIDINVALIDINVALIDINVALIDINVALIDINVALID", "admin@ahsanb.com");
        Set<ConstraintViolation<UserInfo>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.toString().contains("size must be between 1 and 50"));
    }
    
    @Test
    public void testNullRoles() {
        UserInfo user = new UserInfo("admin", "admin123", "admin@ahsanb.com");
        user.setRoles(null);
        Set<ConstraintViolation<UserInfo>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.toString().contains("Roles cannot be null"));
    }
}
