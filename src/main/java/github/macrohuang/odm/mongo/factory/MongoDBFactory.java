package github.macrohuang.odm.mongo.factory;

import github.macrohuang.odm.mongo.exception.MongoDataAccessException;

import com.mongodb.DB;

public interface MongoDBFactory {
	public abstract DB getDB(String dbName) throws MongoDataAccessException;
}
