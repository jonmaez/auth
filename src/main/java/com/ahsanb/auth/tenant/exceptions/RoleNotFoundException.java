package com.ahsanb.auth.tenant.exceptions;

public class RoleNotFoundException extends UserException {

	private static final long serialVersionUID = 4198190626775831366L;

	public RoleNotFoundException(Long id, Throwable cause) {
        super(String.format("Role not found with id: [%s]", id), cause);
    }

    public RoleNotFoundException(Long id) {
        super(String.format("Role not found with id: [%s]", id));
    }

	public RoleNotFoundException(String name, Throwable cause) {
        super(String.format("Role not found with name: [%s]", name), cause);
    }

    public RoleNotFoundException(String name) {
        super(String.format("Role not found with name: [%s]", name));
    }
}

