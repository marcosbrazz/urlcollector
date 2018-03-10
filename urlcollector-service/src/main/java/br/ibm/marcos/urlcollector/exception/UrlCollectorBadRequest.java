package br.ibm.marcos.urlcollector.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class UrlCollectorBadRequest extends RuntimeException {

	private static final long serialVersionUID = 6986194395760498443L;

	public UrlCollectorBadRequest() {
	}

	public UrlCollectorBadRequest(String message) {
		super(message);
	}

	public UrlCollectorBadRequest(Throwable cause) {
		super(cause);
	}

	public UrlCollectorBadRequest(String message, Throwable cause) {
		super(message, cause);
	}

	public UrlCollectorBadRequest(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
