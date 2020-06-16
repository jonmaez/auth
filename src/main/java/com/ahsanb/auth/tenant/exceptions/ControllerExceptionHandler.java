package com.ahsanb.auth.tenant.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import com.ahsanb.auth.tenant.dto.ErrorMessage;

@RestControllerAdvice
public class ControllerExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handlerHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ex) {
        return getErrorResponse("Request method not supported");
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleInvalidArgument(Exception ex) {
        logger.debug(String.format("Invalid request: %s", ex.getMessage()));
        return getErrorResponse("Invalid request");
    }
    
    @ExceptionHandler({
        UserException.class,
    })
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorMessage handleUserException(Exception ex) {
	    logger.debug(String.format("User Exception: %s", ex.getMessage()));
	    return getErrorResponse(ex.getMessage());
	}
    
    @ExceptionHandler({
        UserNotFoundException.class,
    })
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorMessage handleUserNotFoundException(Exception ex) {
	    logger.debug(String.format("User Not Found Exception: %s", ex.getMessage()));
	    return getErrorResponse(ex.getMessage());
	}
    
    @ExceptionHandler({
        InvalidCredentialsException.class,
    })
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorMessage handleInvalidCredentialsException(Exception ex) {
	    logger.debug(String.format("Invalid Credentials Exception: %s", ex.getMessage()));
	    return getErrorResponse(ex.getMessage());
	}
	
    @ExceptionHandler
    public ResponseEntity<ErrorMessage> handleResponseStatusException(ResponseStatusException e) {
        return ResponseEntity.status(e.getStatus()).body(getErrorResponse(e.getReason()));
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorMessage handleAccessDeniedException(Exception ex) {
        logger.debug("Access Denied Exception", ex);
	    return getErrorResponse(ex.getMessage());
    }
    
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage handleInternalException(Exception ex) {
        logger.info("Internal Server Error", ex);
        return getErrorResponse("Internal Server Error");
    }

    private ErrorMessage getErrorResponse(String message) {
        return new ErrorMessage(message);
    }
}
