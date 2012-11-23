package github.macrohuang.odm.mongo.factory;

import github.macrohuang.odm.mongo.constant.Constants;
import github.macrohuang.odm.mongo.exception.MongoDataAccessException;

import org.apache.log4j.Logger;

import com.mongodb.DB;
import com.mongodb.Mongo;

public abstract class AbstractMongoDBFactory implements MongoDBFactory {
	private Mongo dataSource;
	private static final Logger logger = Logger.getLogger(AbstractMongoDBFactory.class);

	public Mongo getDataSource() {
		return dataSource;
	}

	public void setDataSource(Mongo dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public DB getDB(String dbName) throws MongoDataAccessException {
		if (Constants.coreLogEnable)
			logger.info("get db:" + dbName);
		return dataSource.getDB(dbName);
	}
}
