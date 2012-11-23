package github.macrohuang.odm.mongo.exception;

public class MongoDataAccessException extends MongoOdmRuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6532286062958713882L;

	public MongoDataAccessException() {

	}

	public MongoDataAccessException(String desc) {
		super(desc);
	}

	public MongoDataAccessException(Throwable cause) {
		super(cause);
	}

	public MongoDataAccessException(String desc, Throwable cause) {
		super(desc, cause);
	}
}
