package com.ahsanb.auth.tn.config.modelmapper;

import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.AbstractConverter;

import com.ahsanb.auth.tn.entities.Role;
import com.ahsanb.auth.tn.entities.enums.RoleType;

public class RoleListConverter extends AbstractConverter<Set<Role>, Set<RoleType>> {

	@Override
	protected Set<RoleType> convert(Set<Role> source) {
        return source
                .stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
	}
}