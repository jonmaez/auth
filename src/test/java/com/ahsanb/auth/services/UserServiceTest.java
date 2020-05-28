package com.ahsanb.auth.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserServiceTest {
	private static final Long VALID_USER_ID = Long.valueOf(11);
	private static final Long INVALID_USER_ID = Long.valueOf(-99);
	
	private static Role role;

    @Autowired
    private UserService userService;

    @MockBean
    private RoleRepository roleRepository;
    
    @MockBean
    private UserRepository userRepository;
    
    private static PasswordEncoder encoder;
        
    @BeforeAll
    public static void setUp() {
    	encoder = new BCryptPasswordEncoder();
    	
    	role = new Role(RoleType.ROLE_USER);
    }
    
    @Test
    public void givenValidUserInfo_whenAdd_thenUserIsAdded() throws RoleNotFoundException, UserException {
    	UserInfo toAdd = new UserInfo("user", "user123", "user@ahsanb.com");
    	
    	User persisted = new User("user", encoder.encode("user123"), "user@ahsanb.com", new HashSet<Role>(Arrays.asList(role)));
    	
    	when(userRepository.existsByUsername(toAdd.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(toAdd.getEmail())).thenReturn(false);
        when(roleRepository.findByName(any())).thenReturn(Optional.of(role));
    	when(userRepository.save(any(User.class))).thenReturn(persisted);
    	
    	UserInfo returned = userService.addUserInfo(toAdd);
    	
        ArgumentCaptor<User> userArgument = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).existsByUsername(toAdd.getUsername());
        verify(userRepository, times(1)).existsByEmail(toAdd.getEmail());
        verify(roleRepository, times(1)).findByName(any());
        verify(userRepository, times(1)).save(userArgument.capture());
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(roleRepository);
        assertUser(toAdd, userArgument.getValue());
        assertEquals(persisted.getUsername(), returned.getUsername());
    }
    
    @Test
    public void givenExistingUser_whenAddedWithSameUsername_thenUserIsNotAdded() {
    	UserInfo toAdd = new UserInfo("user", "user123", "user@ahsanb.com");
    
    	when(userRepository.existsByUsername(toAdd.getUsername())).thenReturn(true);
    	    	
        Exception exception = assertThrows(UserException.class, () -> {
        	userService.addUserInfo(toAdd);
        });        

        verify(userRepository, times(1)).existsByUsername(toAdd.getUsername());
        verifyNoMoreInteractions(userRepository);
        
        String expectedMessage = String.format("Username [%s] already in use", toAdd.getUsername());
    	String actualMessage = exception.getMessage();
    	
    	assertEquals(actualMessage, expectedMessage);
    }
    
    @Test
    public void givenExistingUser_whenAddedWithSameEmail_thenUserIsNotAdded() {
    	UserInfo toAdd = new UserInfo("user", "user123", "user@ahsanb.com");
    
    	when(userRepository.existsByUsername(toAdd.getUsername())).thenReturn(false);
    	when(userRepository.existsByEmail(toAdd.getEmail())).thenReturn(true);
    	    	
        Exception exception = assertThrows(UserException.class, () -> {
        	userService.addUserInfo(toAdd);
        });        

        verify(userRepository, times(1)).existsByUsername(toAdd.getUsername());
        verify(userRepository, times(1)).existsByEmail(toAdd.getEmail());
        verifyNoMoreInteractions(userRepository);
        
        String expectedMessage = String.format("E-mail [%s] already in use", toAdd.getEmail());
    	String actualMessage = exception.getMessage();
    	
    	assertEquals(actualMessage, expectedMessage);
    }
    
    @Test
    public void givenNonExistingRole_whenAddUser_thenUserIsNotAdded() {
    	UserInfo toAdd = new UserInfo("user", "user123", "user@ahsanb.com");
    
    	when(userRepository.existsByUsername(toAdd.getUsername())).thenReturn(false);
    	when(userRepository.existsByEmail(toAdd.getEmail())).thenReturn(false);
        when(roleRepository.findByName(any())).thenReturn(Optional.empty());
    	    	
        Exception exception = assertThrows(RoleNotFoundException.class, () -> {
        	userService.addUserInfo(toAdd);
        });        

        verify(userRepository, times(1)).existsByUsername(toAdd.getUsername());
        verify(userRepository, times(1)).existsByEmail(toAdd.getEmail());
        verify(roleRepository, times(1)).findByName(any());
        verifyNoMoreInteractions(userRepository);
        
        String expectedMessage = String.format("Role not found with name: [user]");
    	String actualMessage = exception.getMessage();
    	
    	assertEquals(actualMessage, expectedMessage);
    }
    
    @Test
    public void givenInvalidUserInfoWithNullRolesList_whenAddUser_thenUserIsNotAdded() {
    	UserInfo toAdd = new UserInfo("user", "user123", "user@ahsanb.com");
    	toAdd.setRoles(null);
    
    	when(userRepository.existsByUsername(toAdd.getUsername())).thenReturn(false);
    	when(userRepository.existsByEmail(toAdd.getEmail())).thenReturn(false);
    	    	
        Exception exception = assertThrows(UserException.class, () -> {
        	userService.addUserInfo(toAdd);
        });        

        verify(userRepository, times(1)).existsByUsername(toAdd.getUsername());
        verify(userRepository, times(1)).existsByEmail(toAdd.getEmail());
        verifyNoMoreInteractions(userRepository);
        
        String expectedMessage = String.format("Valid role(s) must be specified");
    	String actualMessage = exception.getMessage();
    	
    	assertEquals(actualMessage, expectedMessage);
    }
    
    @Test
    public void givenInvalidUserInfoWithNullRoleInList_whenAddUser_thenUserIsNotAdded() {
    	UserInfo toAdd = new UserInfo("user", "user123", "user@ahsanb.com");
    	Set<RoleType> roles = new HashSet<RoleType>(Arrays.asList(RoleType.ROLE_USER, RoleType.ROLE_MODERATOR, null));
    	toAdd.setRoles(roles);
    
    	when(userRepository.existsByUsername(toAdd.getUsername())).thenReturn(false);
    	when(userRepository.existsByEmail(toAdd.getEmail())).thenReturn(false);
    	    	
        Exception exception = assertThrows(UserException.class, () -> {
        	userService.addUserInfo(toAdd);
        });        

        verify(userRepository, times(1)).existsByUsername(toAdd.getUsername());
        verify(userRepository, times(1)).existsByEmail(toAdd.getEmail());
        verifyNoMoreInteractions(userRepository);
        
        String expectedMessage = String.format("Valid role(s) must be specified");
    	String actualMessage = exception.getMessage();
    	
    	assertEquals(actualMessage, expectedMessage);
    }
    
    @Test
    public void givenExistingUser_whenUpdated_thenUserUpdated() throws UserException {
    	UserInfo toUpdate = new UserInfo("user", "user123", "user123@ahsanb.com");
    	toUpdate.setId(VALID_USER_ID);
    	
    	User updated = new User("user", encoder.encode("user123"), "user@ahsanb.com", new HashSet<Role>(Arrays.asList(role)));
    	updated.setId(VALID_USER_ID);
    	

    	when(userRepository.findById(toUpdate.getId())).thenReturn(Optional.of(updated));
        when(userRepository.findByEmail(toUpdate.getEmail())).thenReturn(Optional.of(updated));
        when(roleRepository.findByName(any())).thenReturn(Optional.of(role));
    	when(userRepository.save(any(User.class))).thenReturn(updated);
    	    	    	
    	UserInfo returned = userService.updateUserInfo(toUpdate, toUpdate.getId());
    	
        ArgumentCaptor<User> userArgument = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).findById(toUpdate.getId());
        verify(userRepository, times(1)).findByEmail(toUpdate.getEmail());
        verify(roleRepository, times(1)).findByName(any());
        verify(userRepository, times(1)).save(userArgument.capture());
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(roleRepository);
        assertUser(toUpdate, userArgument.getValue());
        assertEquals(updated.getUsername(), returned.getUsername());
    }
    
    @Test
    public void givenNonExistingUser_whenUpdated_thenUserIsNotUpdated() {
    	UserInfo toUpdate = new UserInfo("user", "user123", "user123@ahsanb.com");
    
    	when(userRepository.findById(INVALID_USER_ID)).thenReturn(Optional.empty());
    	    	
        Exception exception = assertThrows(UserNotFoundException.class, () -> {
        	userService.updateUserInfo(toUpdate, INVALID_USER_ID);
        });
        
        verify(userRepository, times(1)).findById(INVALID_USER_ID);
        verifyNoMoreInteractions(userRepository);
        
        String expectedMessage = String.format("User not found with id: [%s]", INVALID_USER_ID);;
    	String actualMessage = exception.getMessage();
    	
    	assertEquals(actualMessage, expectedMessage);
    }
    
    @Test
    public void givenExistingUserWithSameEmail_whenUpdated_thenUserIsNotUpdated() {
    	UserInfo toUpdate = new UserInfo("user", "user123", "user@ahsanb.com");
    	toUpdate.setId(VALID_USER_ID);
    	
    	User existing = new User("user", encoder.encode("user123"), "user123@ahsanb.com", new HashSet<Role>(Arrays.asList(role)));
    	existing.setId(VALID_USER_ID);
    	
    	User existingWithSameEmail = new User("user2", encoder.encode("user345"), "user@ahsanb.com", new HashSet<Role>(Arrays.asList(role)));
    	existingWithSameEmail.setId(22L);
    	
    	when(userRepository.findById(toUpdate.getId())).thenReturn(Optional.of(existing));
        when(userRepository.findByEmail(toUpdate.getEmail())).thenReturn(Optional.of(existingWithSameEmail));
    	    	
        Exception exception = assertThrows(UserException.class, () -> {
        	userService.updateUserInfo(toUpdate, toUpdate.getId());
        });
        
        verify(userRepository, times(1)).findById(toUpdate.getId());
        verify(userRepository, times(1)).findByEmail(toUpdate.getEmail());
        verifyNoMoreInteractions(userRepository);
        
        String expectedMessage = String.format("E-mail [%s] already in use", toUpdate.getEmail());
    	String actualMessage = exception.getMessage();
    	
    	assertEquals(actualMessage, expectedMessage);
    }
    
    @Test
    public void whenValidId_thenUserShouldBeFound() throws UserNotFoundException {
    	User toFind = new User("user", encoder.encode("user123"), "user@ahsanb.com", new HashSet<Role>(Arrays.asList(role)));
    	toFind.setId(VALID_USER_ID);
    	
        when(userRepository.findById(toFind.getId())).thenReturn(Optional.of(toFind));
        
        UserInfo found = userService.getUserInfoById(VALID_USER_ID);
        
        verify(userRepository, times(1)).findById(VALID_USER_ID);
        verifyNoMoreInteractions(userRepository);
        assertUser(found, toFind);
    }

    @Test
    public void whenInvalidId_thenUserShouldNotBeFound() {
    	when(userRepository.findById(INVALID_USER_ID)).thenReturn(Optional.empty());
    	
        Exception exception = assertThrows(UserNotFoundException.class, () -> {
        	userService.getUserInfoById(INVALID_USER_ID);
        });
        
        verify(userRepository, times(1)).findById(INVALID_USER_ID);
        verifyNoMoreInteractions(userRepository);
        
        String expectedMessage = String.format("User not found with id: [%s]", INVALID_USER_ID);
    	String actualMessage = exception.getMessage();
    	
    	assertEquals(actualMessage, expectedMessage);
    }
    
    @Test
    public void given3Users_whenGetAll_thenReturn3Records() {
        List<User> allUsers = Arrays.asList(new User("user1", encoder.encode("user1"), "user1@ahsanb.com", new HashSet<Role>(Arrays.asList(role))), 
        									new User("user2", encoder.encode("user2"), "user2@ahsanb.com", new HashSet<Role>(Arrays.asList(role))),
        									new User("user3", encoder.encode("user3"), "user3@ahsanb.com", new HashSet<Role>(Arrays.asList(role))));

        when(userRepository.findAll()).thenReturn(allUsers);
        
        ListUserResponse response = userService.getAllUserInfos();
        verify(userRepository, VerificationModeFactory.times(1)).findAll();
        verifyNoMoreInteractions(userRepository);
        
        assertThat(response.getUsers()).hasSize(3).extracting(UserInfo::getUsername).contains("user1", "user2", "user3");
    }
    
    @Test
    public void whenInvalidId_thenUserShouldNotBeDeleted() {       
        when(userRepository.findById(INVALID_USER_ID)).thenReturn(Optional.empty());
        
        userService.deleteUser(INVALID_USER_ID);
        
        verify(userRepository, times(1)).findById(INVALID_USER_ID);
        verifyNoMoreInteractions(userRepository);
    }
    
    @Test
    public void whenValidId_thenUserShouldBeDeleted() {
    	User deleted = new User("user", encoder.encode("user123"), "user@ahsanb.com", new HashSet<Role>(Arrays.asList(role)));
    	deleted.setId(VALID_USER_ID);
    	
        when(userRepository.findById(VALID_USER_ID)).thenReturn(Optional.of(deleted));
        
        userService.deleteUser(VALID_USER_ID);
        
        verify(userRepository, times(1)).findById(VALID_USER_ID);
        verify(userRepository, times(1)).delete(deleted);
        verifyNoMoreInteractions(userRepository);
    }
    
    private void assertUser(UserInfo expected, User actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getUsername(), actual.getUsername());
        assertEquals(expected.getEmail(), actual.getEmail());
    }
}
