package com.ahsanb.auth.config.modelmapper;

import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.AbstractConverter;

import com.ahsanb.auth.entities.Role;
import com.ahsanb.auth.entities.enums.RoleType;

public class RoleListConverter extends AbstractConverter<Set<Role>, Set<RoleType>> {

	@Override
	protected Set<RoleType> convert(Set<Role> source) {
        return source
                .stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
	}
}