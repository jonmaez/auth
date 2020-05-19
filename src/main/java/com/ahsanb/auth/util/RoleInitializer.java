package com.ahsanb.auth.util;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.ahsanb.auth.dao.RoleRepository;
import com.ahsanb.auth.entities.Role;
import com.ahsanb.auth.entities.enums.RoleType;

@Component
public class RoleInitializer  implements CommandLineRunner {
    private final RoleRepository roleRepository;

    RoleInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        List<Role> roles = Arrays.asList(new Role(RoleType.ROLE_ADMIN), 
					        		  	 new Role(RoleType.ROLE_MODERATOR),
					        		  	 new Role(RoleType.ROLE_USER));
		roles.forEach(role -> roleRepository.save(role));
                
		roleRepository.findAll().forEach(System.out::println);               
    }
}
