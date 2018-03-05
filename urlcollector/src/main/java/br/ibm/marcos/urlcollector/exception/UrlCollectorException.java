package br.ibm.marcos.urlcollector.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class UrlCollectorException extends RuntimeException {

	private static final long serialVersionUID = 4994080492742357709L;

	public UrlCollectorException() {
	}

	public UrlCollectorException(String message) {
		super(message);
	}

	public UrlCollectorException(Throwable cause) {
		super(cause);
	}

	public UrlCollectorException(String message, Throwable cause) {
		super(message, cause);
	}

	public UrlCollectorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
