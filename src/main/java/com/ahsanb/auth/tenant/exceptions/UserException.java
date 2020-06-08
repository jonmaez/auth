package com.ahsanb.auth.tenant.exceptions;

public class UserException extends Exception {

	private static final long serialVersionUID = 637675465056416321L;

	public UserException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserException(String message) {
        super(message);
    }

}

