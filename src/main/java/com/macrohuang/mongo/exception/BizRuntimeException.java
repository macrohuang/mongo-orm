package com.macrohuang.mongo.exception;

public class BizRuntimeException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3828471578369183595L;

	public BizRuntimeException() {
		super();
	}

	public BizRuntimeException(String desc) {
		super(desc);
	}

	public BizRuntimeException(Throwable cause) {
		super(cause);
	}
	public BizRuntimeException(String desc, Throwable cause) {
		super(desc, cause);
	}
}
