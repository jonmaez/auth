package com.ahsanb.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.ahsanb.auth.tn.dao.RoleRepository;
import com.ahsanb.auth.tn.dao.UserRepository;
import com.ahsanb.auth.tn.dto.UserInfo;
import com.ahsanb.auth.tn.entities.Role;
import com.ahsanb.auth.tn.entities.User;
import com.ahsanb.auth.tn.entities.enums.RoleType;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class UserIntegrationTest {
	
	private static final String ROOT_URI = "/users";
	
    @Autowired
    private MockMvc mvc;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;

    private static PasswordEncoder encoder;
    
    @BeforeAll
    public static void setUp() {
    	encoder = new BCryptPasswordEncoder();
    }
    
    @AfterEach
    public void resetDb() {
    	userRepository.deleteAll();
    	roleRepository.deleteAll();
    }
    
    @Test
    public void whenValidAddUser_thenCreateUser() throws Exception {
    	createTestRole(RoleType.ROLE_USER);
    	UserInfo newUser = new UserInfo("user", "user123", "user@ahsanb.com");

        // Not deserializing from newUser object because "password" attribute is ignored
        String payload = "{\"username\":\"user\",\"email\":\"user@ahsanb.com\",\"roles\":[\"user\"],\"password\":\"user123\"}";
        
        mvc.perform(post(ROOT_URI)
           .contentType(MediaType.APPLICATION_JSON)
           .content(payload))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.username", is(newUser.getUsername())));
        
        List<User> found = userRepository.findAll();
        assertThat(found).extracting(User::getUsername).containsOnly("user");
    }
    
    @Test
    public void whenValidUser_thenUpdateUser() throws Exception {
    	Role role = createTestRole(RoleType.ROLE_USER);
    	User existing = createTestUser("user", "user123", "user@ahsanb.com", role);
    	
    	UserInfo toUpdate = new UserInfo("user", "user123", "user123@ahsanb.com");

        // Not deserializing from toUpdate object because "password" attribute is ignored
        String payload = "{\"username\":\"user\",\"email\":\"user123@ahsanb.com\",\"roles\":[\"user\"],\"password\":\"user123\"}";
        
        mvc.perform(post(ROOT_URI + "/" + existing.getId())
           .contentType(MediaType.APPLICATION_JSON)
           .content(payload))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.email", is(toUpdate.getEmail())));
    }
    
    @Test
    public void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
    	Role adminRole = createTestRole(RoleType.ROLE_ADMIN);
    	Role userRole = createTestRole(RoleType.ROLE_USER);
    	Role modRole = createTestRole(RoleType.ROLE_MODERATOR);
    	
        User admin = createTestUser("admin", "admin", "admin@ahsanb.com", adminRole);
        User user = createTestUser("user", "user", "user@ahsanb.com", userRole);
        User moderator = createTestUser("moderator", "moderator", "moderator@ahsanb.com", modRole);

        mvc.perform(get(ROOT_URI)
           .contentType(MediaType.APPLICATION_JSON))
	       .andExpect(status().isOk())
	       .andExpect(jsonPath("$.users", hasSize(3)))
	       .andExpect(jsonPath("$.users[0].username", is(admin.getUsername())))
	       .andExpect(jsonPath("$.users[1].username", is(user.getUsername())))
	       .andExpect(jsonPath("$.users[2].username", is(moderator.getUsername())));
    }
    
    @Test
    public void whenExistingUser_thenGetUser() throws Exception {
    	Role role = createTestRole(RoleType.ROLE_USER);
        User existing = createTestUser("user", "user123", "user@ahsanb.com", role);
    	     
        mvc.perform(get(ROOT_URI + "/" + existing.getId())
           .contentType(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.username", is(existing.getUsername())));
    }
    
    @Test
    public void whenExistingUser_thenDeleteUser() throws Exception {
    	Role role = createTestRole(RoleType.ROLE_USER);
        User existing = createTestUser("user", "user123", "user@ahsanb.com", role);
        
        mvc.perform(delete(ROOT_URI + "/" + existing.getId())
           .contentType(MediaType.APPLICATION_JSON))
           .andExpect(status().is(204))
           .andExpect(jsonPath("$").doesNotExist());       
    }
    
    private Role createTestRole(RoleType roleType) {
    	Role role = new Role(roleType);
        return roleRepository.saveAndFlush(role);    
    }
    
    private User createTestUser(String username, String password, String email, Role role) {
    	Set<Role> roles = new HashSet<Role>();
    	roles.add(role);
    	User user = new User(username, encoder.encode(password), email, roles);
        return userRepository.saveAndFlush(user);    
    }    
}
