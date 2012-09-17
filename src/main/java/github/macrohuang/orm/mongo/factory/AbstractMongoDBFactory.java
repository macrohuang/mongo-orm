package github.macrohuang.orm.mongo.factory;

import github.macrohuang.orm.mongo.constant.Constants;
import github.macrohuang.orm.mongo.exception.MongoDataAccessException;

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
