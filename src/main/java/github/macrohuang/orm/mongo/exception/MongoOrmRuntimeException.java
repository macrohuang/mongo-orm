package github.macrohuang.orm.mongo.exception;

public class MongoOrmRuntimeException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3828471578369183595L;

	public MongoOrmRuntimeException() {
		super();
	}

	public MongoOrmRuntimeException(String desc) {
		super(desc);
	}

	public MongoOrmRuntimeException(Throwable cause) {
		super(cause);
	}
	public MongoOrmRuntimeException(String desc, Throwable cause) {
		super(desc, cause);
	}
}
