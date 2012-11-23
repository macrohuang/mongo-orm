package github.macrohuang.odm.mongo.exception;

public class MongoOdmRuntimeException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3828471578369183595L;

	public MongoOdmRuntimeException() {
		super();
	}

	public MongoOdmRuntimeException(String desc) {
		super(desc);
	}

	public MongoOdmRuntimeException(Throwable cause) {
		super(cause);
	}
	public MongoOdmRuntimeException(String desc, Throwable cause) {
		super(desc, cause);
	}
}
