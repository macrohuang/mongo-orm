package com.macrohuang.mongo;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

import com.macrohuang.mongo.annotation.Document;
import com.macrohuang.mongo.exception.MongoDataAccessException;
import com.macrohuang.mongo.util.DBObjectUtil;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class MongoDBTemplate<T extends Document> {
	private MongoDBFactory dbFactory;
	private int batchSize = 1000;

	public int getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	public MongoDBFactory getDbFactory() {
		return dbFactory;
	}

	public void setDbFactory(MongoDBFactory dbFactory) {
		this.dbFactory = dbFactory;
	}

	private DBCollection getCollection(T entry) throws MongoDataAccessException {
		if (entry == null)
			throw new MongoDataAccessException("Entry can not be null");
		Annotation docAno = entry.getClass().getAnnotation(Document.class);
		if (docAno == null) {
			throw new MongoDataAccessException("Entry does not mapped by mongo.");
		}
		Document document = (Document) docAno;
		DB db = dbFactory.getDB(document.db());
		if (!StringUtils.hasText(document.collection())) {
			return db.getCollection(document.collection());
		} else {
			String className = entry.getClass().getName();
			if (className.contains(".")) {
				className = className.substring(className.lastIndexOf("."));
			}
			return db.getCollection(className.toLowerCase());
		}
	}

	@SuppressWarnings("unchecked")
	public List<T> findByExample(T entry) throws MongoDataAccessException {
		DBCollection collection = getCollection(entry);
		DBCursor dbCursor = collection.find(DBObjectUtil.convertPO2DBObject(entry));
		dbCursor.batchSize(batchSize);
		List<T> result = new ArrayList<T>();
		for (DBObject object : dbCursor) {
			try {
				result.add(DBObjectUtil.fillDocument2PO(object, (T) entry.getClass().newInstance()));
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}
}
