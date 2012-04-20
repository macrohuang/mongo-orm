package com.macrohuang.mongo;

import com.macrohuang.mongo.exception.MongoDataAccessException;
import com.mongodb.DB;

public interface MongoDBFactory {
	public abstract DB getDB(String dbName) throws MongoDataAccessException;
}
