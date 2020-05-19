package com.ahsanb.auth.entities.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum RoleType {
	ROLE_USER("user"),
    ROLE_MODERATOR("mod"),
    ROLE_ADMIN("admin");
    
    private String roleName;
    
    private RoleType(String roleName) {
        this.roleName = roleName;
    }
    
    @JsonCreator
    public static RoleType fromRoleName(String roleName) {
        for (RoleType value : RoleType.values()) {
            if (roleName.equals(value.roleName)) {
                return value;
            }
        }
        return null;
    }
    
    @JsonValue
    public String getRoleName() {
        return roleName;
    }
}
