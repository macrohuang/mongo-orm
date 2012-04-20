package com.macrohuang.mongo.factory;

import com.macrohuang.mongo.MongoConfig;
import com.macrohuang.mongo.exception.MongoDatasourceException;
import com.mongodb.Mongo;

public abstract class AbstractMongoDatasourceFactory {

	public abstract Mongo getMongoDatasource(String host, int port) throws MongoDatasourceException;

	public abstract Mongo getMongoDatasource(MongoConfig config) throws MongoDatasourceException;
}
