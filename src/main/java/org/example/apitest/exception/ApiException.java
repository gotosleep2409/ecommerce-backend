package org.example.apitest.exception;

import org.springframework.validation.FieldError;

import java.util.List;

public class ApiException extends Exception {
	private static final long serialVersionUID = 1L;

	/** The error message. */
	private final ErrorMessage errorMessage;

	private List<ErrorDetail> errorDetails;

	/**
	 * Instantiates a new epic exception.
	 *
	 * @param pErrorMessage the error message
	 */
	public ApiException(ErrorMessage pErrorMessage) {
		super();
		errorMessage = pErrorMessage;
	}

	public ApiException(String message) {
		super(message);
		errorMessage = ErrorMessage.BAD_REQUEST;
	}

	public ApiException() {
		super();
		errorMessage = null;
	}

	public ApiException(ErrorMessage pErrorMessage, List<FieldError> fieldErrors) {
		super();
		errorMessage = pErrorMessage;
		if (fieldErrors != null)
			errorDetails = ErrorDetail.convertFromFieldErrors(fieldErrors);
	}

	/**
	 * Gets the error message.
	 * 
	 * @return the code
	 */
	public ErrorMessage getErrorMessage() {
		return errorMessage;
	}

	public List<ErrorDetail> getErrorDetails() {
		return errorDetails;
	}

	public void setErrorDetails(List<ErrorDetail> errorDetails) {
		this.errorDetails = errorDetails;
	}

}
