package com.ahsanb.auth.exceptions;

public class UserNotFoundException extends UserException {

	private static final long serialVersionUID = 4198190626775831366L;

	public UserNotFoundException(Long id, Throwable cause) {
        super(String.format("User not found with id: [%s]", id), cause);
    }

    public UserNotFoundException(Long id) {
        super(String.format("User not found with id: [%s]", id));
    }

	public UserNotFoundException(String name, Throwable cause) {
        super(String.format("User not found with name: [%s]", name), cause);
    }

    public UserNotFoundException(String name) {
        super(String.format("User not found with name: [%s]", name));
    }
}

