package github.macrohuang.orm.mongo.factory;

import github.macrohuang.orm.mongo.exception.MongoDataAccessException;

import com.mongodb.DB;
import com.mongodb.Mongo;

public abstract class AbstractMongoDBFactory implements MongoDBFactory {
	private Mongo dataSource;

	public Mongo getDataSource() {
		return dataSource;
	}

	public void setDataSource(Mongo dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public DB getDB(String dbName) throws MongoDataAccessException {
		return dataSource.getDB(dbName);
	}
}
