package com.github.hiroshi_cl.sa.withSentinel;

public class MultipleSentinelsException extends Exception {

	private static final long serialVersionUID = -4096034914279265500L;

	public MultipleSentinelsException() {
		super();
	}

	public MultipleSentinelsException(String message, Throwable cause) {
		super(message, cause);
	}

	public MultipleSentinelsException(String message) {
		super(message);
	}

	public MultipleSentinelsException(Throwable cause) {
		super(cause);
	}

}