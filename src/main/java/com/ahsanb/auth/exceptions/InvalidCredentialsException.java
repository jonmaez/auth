package com.ahsanb.auth.exceptions;

public class InvalidCredentialsException extends Exception {

	private static final long serialVersionUID = 637675465056416321L;

	public InvalidCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidCredentialsException(String message) {
        super(message);
    }

}

