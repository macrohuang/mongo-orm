package com.macrohuang.mongo.factory;

import java.net.UnknownHostException;

import com.macrohuang.mongo.MongoConfig;
import com.macrohuang.mongo.exception.MongoDatasourceException;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;

public class DefaultMongoDatasourceFactory extends AbstractMongoDatasourceFactory {

	@Override
	public Mongo getMongoDatasource(String host, int port) {
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

	@Override
	public Mongo getMongoDatasource(MongoConfig config) throws RuntimeException {
		try {
			return new Mongo(new ServerAddress(config.getHost(), config.getPort()), config);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			throw new MongoDatasourceException(e);
		}
	}
}
