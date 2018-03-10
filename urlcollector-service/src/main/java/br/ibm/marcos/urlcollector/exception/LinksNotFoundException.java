package br.ibm.marcos.urlcollector.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class LinksNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -894436470046774086L;

	public LinksNotFoundException() {
	}

	public LinksNotFoundException(String message) {
		super(message);
	}

	public LinksNotFoundException(Throwable cause) {
		super(cause);
	}

	public LinksNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public LinksNotFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
