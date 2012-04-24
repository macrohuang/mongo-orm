package github.macrohuang.orm.mongo.factory;

import github.macrohuang.orm.mongo.exception.MongoDataAccessException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.InitializingBean;

import com.mongodb.DB;

public class DefaultMongoDBFactory implements MongoDBFactory, InitializingBean {
	private AbstractMongoDatasourceFactory datasourceFactory;
	private Map<String, DB> dbMap = new ConcurrentHashMap<String, DB>();

	public AbstractMongoDatasourceFactory getDatasourceFactory() {
		return datasourceFactory;
	}

	public void setDatasourceFactory(AbstractMongoDatasourceFactory datasourceFactory) {
		this.datasourceFactory = datasourceFactory;
	}

	@Override
	public DB getDB(String dbName) throws MongoDataAccessException {
		return dbMap.get(dbName);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		DB db;
		if (datasourceFactory.getConfig().isNeedAuth()) {
			db = datasourceFactory.getMongoDatasource().getDB("admin");
			if (!db.isAuthenticated()
					&& !db.authenticate(datasourceFactory.getConfig().getUsername(), datasourceFactory.getConfig().getPassword().toCharArray())) {
				throw new MongoDataAccessException(String.format("Can't access db: %s with user:%s, password:%s", datasourceFactory.getConfig()
						.getHost(), datasourceFactory.getConfig().getUsername(), datasourceFactory.getConfig().getPassword()));
			}
		}
		if (datasourceFactory.getInclude() != null && datasourceFactory.getInclude().size() > 0) {
			for (String dbname : datasourceFactory.getInclude()) {
				dbMap.put(dbname, datasourceFactory.getMongoDatasource().getDB(dbname));
			}
		} else {
			for (String dbname : datasourceFactory.getMongoDatasource().getDatabaseNames()) {
				dbMap.put(dbname, datasourceFactory.getMongoDatasource().getDB(dbname));
			}
		}
	}

}
