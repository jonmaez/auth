package com.ahsanb.auth.tenant.config.modelmapper;

import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.AbstractConverter;

import com.ahsanb.auth.tenant.entities.Role;

public class RoleListConverter extends AbstractConverter<Set<Role>, Set<String>> {

	@Override
	protected Set<String> convert(Set<Role> source) {
        return source
                .stream()
                .map(Role::getRole)
                .collect(Collectors.toSet());
	}
}