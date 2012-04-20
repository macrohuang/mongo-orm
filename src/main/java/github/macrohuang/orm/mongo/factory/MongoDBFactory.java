package github.macrohuang.orm.mongo.factory;

import github.macrohuang.orm.mongo.exception.MongoDataAccessException;

import com.mongodb.DB;

public interface MongoDBFactory {
	public abstract DB getDB(String dbName) throws MongoDataAccessException;
}
