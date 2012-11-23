package github.macrohuang.orm.mongo.exception;

public class MongoDatasourceException extends MongoOrmRuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3355667233734714013L;

	public MongoDatasourceException(String desc, Throwable orig) {
		super(desc, orig);
	}

	public MongoDatasourceException(String desc) {
		super(desc);
	}

	public MongoDatasourceException(Throwable orig) {
		super(orig);
	}

	public MongoDatasourceException() {
		super();
	}
}
