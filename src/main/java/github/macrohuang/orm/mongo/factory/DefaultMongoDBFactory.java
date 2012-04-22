package github.macrohuang.orm.mongo.factory;

import github.macrohuang.orm.mongo.exception.MongoDataAccessException;

import com.mongodb.DB;


public class DefaultMongoDBFactory implements MongoDBFactory {
	private AbstractMongoDatasourceFactory datasourceFactory;

	public AbstractMongoDatasourceFactory getDatasourceFactory() {
		return datasourceFactory;
	}

	public void setDatasourceFactory(AbstractMongoDatasourceFactory datasourceFactory) {
		this.datasourceFactory = datasourceFactory;
	}

	@Override
	public DB getDB(String dbName) throws MongoDataAccessException {
		return datasourceFactory.getMongoDatasource().getDB(dbName);
	}

}
