package github.macrohuang.orm.mongo.factory;

import github.macrohuang.orm.mongo.constant.Constants;
import github.macrohuang.orm.mongo.exception.MongoDataAccessException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

import com.mongodb.DB;

public class DefaultMongoDBFactory implements MongoDBFactory, InitializingBean {
	private AbstractMongoDatasourceFactory datasourceFactory;
	private Map<String, DB> dbMap = new ConcurrentHashMap<String, DB>();
	private DB defaultDb;
	private final Logger logger = Logger.getLogger(DefaultMongoDBFactory.class);

	public AbstractMongoDatasourceFactory getDatasourceFactory() {
		return datasourceFactory;
	}

	public void setDatasourceFactory(AbstractMongoDatasourceFactory datasourceFactory) {
		this.datasourceFactory = datasourceFactory;
	}

	@Override
	public DB getDB(String dbName) throws MongoDataAccessException {
		if (Constants.coreLogEnable)
			logger.info("get db:" + dbName);
		return dbMap.containsKey(dbName) ? dbMap.get(dbName) : defaultDb;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		DB db;
		if (datasourceFactory.getConfig().isNeedAuth()) {
			logger.info("need auth");
			db = datasourceFactory.getMongoDatasource().getDB("admin");
			if (!db.isAuthenticated()
					&& !db.authenticate(datasourceFactory.getConfig().getUsername(), datasourceFactory.getConfig().getPassword().toCharArray())) {
				throw new MongoDataAccessException(String.format("Can't access db: %s with user:%s, password:%s", datasourceFactory.getConfig()
						.getHost(), datasourceFactory.getConfig().getUsername(), datasourceFactory.getConfig().getPassword()));
			}
			logger.info("auth pass");
		}
		if (datasourceFactory.getInclude() != null && datasourceFactory.getInclude().size() > 0) {
			logger.info("manually specify db list: " + datasourceFactory.getInclude());
			for (String dbname : datasourceFactory.getInclude()) {
				dbMap.put(dbname, datasourceFactory.getMongoDatasource().getDB(dbname));
			}
		} else {
			logger.info("default all dbs in the host: " + datasourceFactory.getMongoDatasource().getDatabaseNames());
			for (String dbname : datasourceFactory.getMongoDatasource().getDatabaseNames()) {
				dbMap.put(dbname, datasourceFactory.getMongoDatasource().getDB(dbname));
			}
		}
		if (defaultDb == null) {// If the specify db doesn't exists, then
			// return the local db instead.
			defaultDb = datasourceFactory.getMongoDatasource().getDB("default");
		}
	}

}
