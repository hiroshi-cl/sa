package com.github.hiroshi_cl.sa.withSentinel;

public class NoSentinelException extends Exception {

	private static final long serialVersionUID = -5388291329809150852L;

	public NoSentinelException() {
		super();
	}

	public NoSentinelException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoSentinelException(String message) {
		super(message);
	}

	public NoSentinelException(Throwable cause) {
		super(cause);
	}

}