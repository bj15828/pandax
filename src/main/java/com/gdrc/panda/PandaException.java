package com.gdrc.panda;

/**
 * 
 * from Gondola
 * 
 * 
 */

public class PandaException extends Exception {

	public enum Code {
		/**
		 * Used for any generic error condition. The message should provide
		 * details of the error.
		 */
		ERROR(""), NOT_LEADER("The leader is %s"), SAME_SHARD("This slave (%d) and the master (%d) cannot be in the same shard (%s)"), SLAVE_MODE("This operation is not allowed while the member (%d) is in slave mode");

		private String messageTemplate;

		Code(String messageTemplate) {
			this.messageTemplate = messageTemplate;
		}

		public String messageTemplate() {
			return messageTemplate;
		}
	}

	private Code code;

	public PandaException(Throwable cause) {
		super(cause.getMessage(), cause);
		// Inherit the code from the most recent GondolaException in the chain,
		// if any
		while (cause != null) {
			if (cause instanceof PandaException) {
				PandaException ge = (PandaException) cause;
				code = ge.code;
				break;
			}
			cause = cause.getCause();
		}
		if (code == null) {
			code = Code.ERROR;
		}
	}

	public PandaException(String message) {
		super(message);
		this.code = Code.ERROR;
	}

	public PandaException(String message, Throwable cause) {
		super(message, cause);
		this.code = Code.ERROR;
	}

	public PandaException(Code code, Object... args) {
		super(String.format(code.messageTemplate(), args));
		this.code = code;
	}

	public Code getCode() {
		return code;
	}
}
