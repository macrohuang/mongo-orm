package github.macrohuang.orm.mongo.factory;

import github.macrohuang.orm.mongo.config.MongoConfig;
import github.macrohuang.orm.mongo.exception.MongoDatasourceException;

import java.net.UnknownHostException;

import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;

public abstract class AbstractMongoDatasourceFactory {
	protected MongoConfig config;

	public AbstractMongoDatasourceFactory(MongoConfig config) {
		this.config = config;
	}

	public Mongo getMongoDatasource(String host, int port) throws MongoDatasourceException {
		try {
			return new Mongo(host, port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			throw new MongoDatasourceException(e);
		} catch (MongoException e) {
			e.printStackTrace();
			throw new MongoDatasourceException(e);
		}
	}

	public Mongo getMongoDatasource() throws MongoDatasourceException {
		try {
			return new Mongo(new ServerAddress(config.getHost(), config.getPort()), config);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			throw new MongoDatasourceException(e);
		}
	}
}
