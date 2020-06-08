package com.ahsanb.auth.tn.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.ahsanb.auth.tn.dao.RoleRepository;
import com.ahsanb.auth.tn.dao.UserRepository;
import com.ahsanb.auth.tn.entities.Role;
import com.ahsanb.auth.tn.entities.User;
import com.ahsanb.auth.tn.entities.enums.RoleType;

@Component
@ConditionalOnProperty(value = "users.initializer.enabled", havingValue = "true", matchIfMissing = false)
public class UserInitializer implements CommandLineRunner {
	
	@Autowired
	PasswordEncoder encoder;
	
    private final RoleRepository roleRepository;
    
    private final UserRepository userRepository;

    UserInitializer(RoleRepository roleRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        List<Role> roles = Arrays.asList(new Role(RoleType.ROLE_ADMIN), 
					        		  	 new Role(RoleType.ROLE_MODERATOR),
					        		  	 new Role(RoleType.ROLE_USER));
		roles.forEach(role -> roleRepository.save(role));
                
		roleRepository.findAll().forEach(System.out::println);
		
        Stream.of(new User("admin", encoder.encode("admin123"), "admin@ahsanb.com", new HashSet<Role>(Arrays.asList(roles.get(0)))),
        		  new User("moderator", encoder.encode("mod123"), "mod@ahsanb.com", new HashSet<Role>(Arrays.asList(roles.get(1)))), 
        		  new User("user", encoder.encode("user123"), "user@ahsanb.com", new HashSet<Role>(Arrays.asList(roles.get(2)))))
        	  .forEach(user -> userRepository.save(user));
    
        userRepository.findAll().forEach(System.out::println);
    }
}
