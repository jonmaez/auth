package com.ahsanb.auth.config.modelmapper;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ahsanb.auth.dto.UserInfo;
import com.ahsanb.auth.entities.User;

/**
 *  Configuration class which provides the model mapper beans for the below scenarios:
 *  1) UserInfo from User
 */
@Configuration
public class UserModelMapperConfig {

    @Bean("UserRetriever")
    public ModelMapper userInfoFromUser() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.createTypeMap(User.class, UserInfo.class).
			        addMappings(mapper -> mapper.using(new RoleListConverter())
			        		.map(User::getRoles, UserInfo::setRoles))
			        .addMapping(User::getId, UserInfo::setId);
             
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        return modelMapper;
    }
}