package org.cl.demo.exceptions;

public class MetierException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MetierException() {
		super();
	}

	public MetierException(String message, Throwable cause) {
		super(message, cause);
	}

	public MetierException(String message) {
		super(message);
	}

	public MetierException(Throwable cause) {
		super(cause);
	}

}
