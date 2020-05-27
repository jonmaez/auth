package com.ahsanb.auth.entities;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ahsanb.auth.entities.enums.RoleType;

public class UserValidationTest {
	
	private static Set<Role> roles;
	
    private static Validator validator;
    
	private static PasswordEncoder encoder;

    @BeforeAll
    public static void setUp() {
    	encoder = new BCryptPasswordEncoder();
    	
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        
        roles = new HashSet<Role>();
        roles.add(new Role(RoleType.ROLE_ADMIN));
    }
    
    @Test
    public void testValidUser() {
        User user = new User("admin", encoder.encode("admin123"), "admin@ahsanb.com", roles);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }
    
    @Test
    public void testEmptyUsername() {
        User user = new User("", encoder.encode("admin123"), "admin@ahsanb.com", roles);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.toString().contains("size must be between 3 and 20"));
    }

    @Test
    public void testNullUsername() {
        User user = new User(null, encoder.encode("admin123"), "admin@ahsanb.com", roles);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.toString().contains("Username cannot be null"));
    }
    
    @Test
    public void testVeryLongUsername() {
        User user = new User("INVALIDINVALIDINVALIDINVALIDINVALID", encoder.encode("admin123"), "admin@ahsanb.com", roles);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.toString().contains("size must be between 3 and 20"));
    }
    
    @Test
    public void testInvalidEmail() {
        User user = new User("admin", encoder.encode("admin123"), "INVALID", roles);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.toString().contains("E-mail must be valid"));
    }
    
    @Test
    public void testEmptyEmail() {
        User user = new User("admin", encoder.encode("admin123"), "", roles);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.toString().contains("size must be between 1 and 50"));
    }

    @Test
    public void testNullEmail() {
        User user = new User("admin", encoder.encode("admin123"), null, roles);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.toString().contains("E-mail cannot be null"));
    }
    
    @Test
    public void testVeryLongEmail() {
        User user = new User("admin", encoder.encode("admin123"), "adminadminadminadminadminadminadminadminadminadmin@ahsanb.com", roles);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.toString().contains("size must be between 1 and 50"));
    }
    
    @Test
    public void testEmptyPassword() {
        User user = new User("admin", "", "admin@ahsanb.com", roles);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.toString().contains("Password cannot be empty"));
    }

    @Test
    public void testNullPassword() {
        User user = new User("admin", null, "admin@ahsanb.com", roles);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.toString().contains("Password cannot be null"));
    }
    
    @Test
    public void testNullRoles() {
        User user = new User("admin", encoder.encode("admin123"), "admin@ahsanb.com", null);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.toString().contains("Roles cannot be null"));
    }
}
