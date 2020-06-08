package com.ahsanb.auth;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.emptyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashSet;
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

import com.ahsanb.auth.tenant.dao.RoleRepository;
import com.ahsanb.auth.tenant.dao.UserRepository;
import com.ahsanb.auth.tenant.dto.LoginRequest;
import com.ahsanb.auth.tenant.entities.Role;
import com.ahsanb.auth.tenant.entities.User;
import com.ahsanb.auth.tenant.entities.enums.RoleType;
import com.ahsanb.auth.util.JsonUtil;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class AuthIntegrationTest {
	
	private static final String ROOT_URI = "/auth";
	
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
    public void givenUser_whenLogin_thenSuccessfulLogin() throws Exception {
    	Role adminRole = createTestRole(RoleType.ROLE_ADMIN);   	
        User adminUser = createTestUser("admin", "admin", "admin@ahsanb.com", adminRole);
    	LoginRequest loginRequest = new LoginRequest("admin", "admin");

        mvc.perform(post(ROOT_URI + "/signin")
           .contentType(MediaType.APPLICATION_JSON)
           .content(JsonUtil.asJsonString(loginRequest)))
	       .andExpect(status().isOk())
	       .andExpect(jsonPath("$.token", is(not(nullValue()))))
	       .andExpect(jsonPath("$.token", is(not(emptyString()))))
	       .andExpect(jsonPath("$.username", is(adminUser.getUsername())))
	       .andExpect(jsonPath("$.email", is(adminUser.getEmail())))
	       .andExpect(jsonPath("$.type", is("Bearer")))
	       .andExpect(jsonPath("$.roles[0]", is(adminRole.getName().getRoleName())));
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
