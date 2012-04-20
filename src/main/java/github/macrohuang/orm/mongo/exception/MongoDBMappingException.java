package github.macrohuang.orm.mongo.exception;

public class MongoDBMappingException extends BizRuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2078221308198293038L;

	public MongoDBMappingException(String desc, Throwable orig) {
		super(desc, orig);
	}

	public MongoDBMappingException(String desc) {
		super(desc);
	}

	public MongoDBMappingException(Throwable orig) {
		super(orig);
	}

	public MongoDBMappingException() {
		super();
	}
}
