package com.ahsanb.auth.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.ahsanb.auth.tenant.dao.UserRepository;
import com.ahsanb.auth.tenant.entities.Role;
import com.ahsanb.auth.tenant.entities.User;
import com.ahsanb.auth.tenant.entities.enums.RoleType;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class UserRepositoryTest {
	
	private Role role;
	
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private UserRepository userRepository;
    	
	private PasswordEncoder encoder;
	
    @BeforeEach
    public void setUp() {
    	encoder = new BCryptPasswordEncoder();
    	
        role = new Role(RoleType.ROLE_ADMIN);
        entityManager.persistAndFlush(role);
    }
    
    @Test
    public void whenFindByEmail_thenReturnUser() {
        User user = new User("admin", encoder.encode("admin123"), "admin@ahsanb.com", new HashSet<Role>(Arrays.asList(role)));
        entityManager.persistAndFlush(user);

        User found = userRepository.findByEmail(user.getEmail()).get();
        assertThat(found.getEmail()).isEqualTo(user.getEmail());
    }
    
    @Test
    public void whenFindByInvalidEmail_thenReturnNull() {
        User user = new User("admin", encoder.encode("admin123"), "admin@ahsanb.com", new HashSet<Role>(Arrays.asList(role)));
        entityManager.persistAndFlush(user);

        User fromDb = userRepository.findByEmail("doesNotExist@ahsanb.com").orElse(null);
        assertThat(fromDb).isNull();
    }

    @Test
    public void whenExistsByEmail_thenReturnTrue() {
        User user = new User("admin", encoder.encode("admin123"), "admin@ahsanb.com", new HashSet<Role>(Arrays.asList(role)));
        entityManager.persistAndFlush(user);

        assertThat(userRepository.existsByEmail(user.getEmail())).isTrue();
    }
    
    @Test
    public void whenExistsByInvalidEmail_thenReturnFalse() {
        User user = new User("admin", encoder.encode("admin123"), "admin@ahsanb.com", new HashSet<Role>(Arrays.asList(role)));
        entityManager.persistAndFlush(user);

        assertThat(userRepository.existsByEmail("doesNotExist@ahsanb.com")).isFalse();
    }
    
    @Test
    public void whenExistsByUsername_thenReturnTrue() {
        User user = new User("admin", encoder.encode("admin123"), "admin@ahsanb.com", new HashSet<Role>(Arrays.asList(role)));
        entityManager.persistAndFlush(user);

        assertThat(userRepository.existsByUsername(user.getUsername())).isTrue();
    }
    
    @Test
    public void whenExistsByInvalidUsername_thenReturnFalse() {
        User user = new User("admin", encoder.encode("admin123"), "admin@ahsanb.com", new HashSet<Role>(Arrays.asList(role)));
        entityManager.persistAndFlush(user);

        assertThat(userRepository.existsByUsername("doesNotExist")).isFalse();
    }
    
    @Test
    public void whenFindByExistingId_thenReturnUser() {
        User user = new User("admin", encoder.encode("admin123"), "admin@ahsanb.com", new HashSet<Role>(Arrays.asList(role)));
        entityManager.persistAndFlush(user);

        User fromDb = userRepository.findById(user.getId()).orElse(null);
        assertThat(fromDb.getUsername()).isEqualTo(user.getUsername());
    }

    @Test
    public void whenFindByNonExistingId_thenReturnNull() {
        User fromDb = userRepository.findById(11L).orElse(null);
        assertThat(fromDb).isNull();
    }
    
    @Test
    public void givenSetOfUsers_whenFindAll_thenReturnAllUsers() {
        User user1 = new User("user1", encoder.encode("admin123"), "user1@ahsanb.com", new HashSet<Role>(Arrays.asList(role)));
        User user2 = new User("user2", encoder.encode("admin123"), "user2@ahsanb.com", new HashSet<Role>(Arrays.asList(role)));
        User user3 = new User("user3", encoder.encode("admin123"), "user3@ahsanb.com", new HashSet<Role>(Arrays.asList(role)));

        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(user3);
        entityManager.flush();

        List<User> allUsers = userRepository.findAll();

        assertThat(allUsers).hasSize(3).extracting(User::getUsername).
        						containsOnly(user1.getUsername(), user2.getUsername(), user3.getUsername());
    }
    
    @Test
    public void whenSaveWithExistingUsername_thenThrowsException() {
        User existing = new User("user1", encoder.encode("admin123"), "existing@ahsanb.com", new HashSet<Role>(Arrays.asList(role)));
        entityManager.persistAndFlush(existing);

        User newUser = new User("user1", encoder.encode("admin123"), "new_user@ahsanb.com", new HashSet<Role>(Arrays.asList(role)));
        
        assertThrows(DataIntegrityViolationException.class, () -> {
        	userRepository.save(newUser);
        });        
    }
    
    @Test
    public void whenSaveWithExistingEmail_thenThrowsException() {
        User existing = new User("user1", encoder.encode("admin123"), "existing@ahsanb.com", new HashSet<Role>(Arrays.asList(role)));
        entityManager.persistAndFlush(existing);

        User newUser = new User("user2", encoder.encode("admin123"), "existing@ahsanb.com", new HashSet<Role>(Arrays.asList(role)));
        
        assertThrows(DataIntegrityViolationException.class, () -> {
        	userRepository.save(newUser);
        });        
    }
}
