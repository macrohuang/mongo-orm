package github.macrohuang.orm.mongo.core;

import github.macrohuang.orm.mongo.annotation.Document;
import github.macrohuang.orm.mongo.exception.MongoDataAccessException;
import github.macrohuang.orm.mongo.factory.MongoDBFactory;
import github.macrohuang.orm.mongo.query.Page;
import github.macrohuang.orm.mongo.query.Query;
import github.macrohuang.orm.mongo.util.DBObjectUtil;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

public class MongoDBTemplate {
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

	private DBCollection getCollection(Class<?> class1) {
		Document document = class1.getAnnotation(Document.class);
		if (document == null) {
			throw new MongoDataAccessException("Entry does not mapped by mongo.");
		}
		DB db = dbFactory.getDB(document.db());
		if (StringUtils.hasText(document.collection())) {
			return db.getCollection(document.collection());
		} else {
			String className = class1.getName();
			if (className.contains(".")) {
				className = className.substring(className.lastIndexOf("."));
			}
			return db.getCollection(className.toLowerCase());
		}
	}
	private <T> DBCollection getCollection(T entry) throws MongoDataAccessException {
		if (entry == null)
			throw new MongoDataAccessException("Entry can not be null");
		return getCollection(entry.getClass());
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> findByExample(T entry) throws MongoDataAccessException {
		Assert.assertNotNull(entry);
		DBCollection collection = getCollection(entry);
		DBCursor dbCursor = collection.find(DBObjectUtil.convertPO2DBObject(entry));
		dbCursor.batchSize(batchSize);
		List<T> result = new ArrayList<T>();
		for (DBObject object : dbCursor) {
			try {
				result.add(DBObjectUtil.fillDocument2PO(object, (T) entry.getClass().newInstance()));
			} catch (InstantiationException e) {
				e.printStackTrace();
				throw new MongoDataAccessException("can't instance a interface or abstract class", e);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				throw new MongoDataAccessException(e);
			}
		}
		return result;
	}

	public <T> boolean save(T entry) throws MongoDataAccessException {
		Assert.assertNotNull(entry);
		DBCollection collection = getCollection(entry);
		return returnResult(collection.insert(DBObjectUtil.convertPO2DBObject(entry)));
	}

	public <T> boolean delete(T entry) throws MongoDataAccessException {
		Assert.assertNotNull(entry);
		DBCollection collection = getCollection(entry);
		return returnResult(collection.remove(DBObjectUtil.convertPO2DBObject(entry)));
	}

	public <T> boolean saveAll(List<T> entrys) throws MongoDataAccessException {
		Assert.assertNotNull(entrys);
		Assert.assertNotEmpty(entrys);
		DBCollection collection = getCollection(entrys.get(0));
		List<DBObject> list = new ArrayList<DBObject>();
		for (T entry : entrys) {
			list.add(DBObjectUtil.convertPO2DBObject(entry));
		}
		return returnResult(collection.insert(list));
	}

	public <T> boolean deleteAll(List<T> entrys) throws MongoDataAccessException {
		Assert.assertNotNull(entrys);
		Assert.assertNotEmpty(entrys);
		DBCollection collection = getCollection(entrys.get(0));
		boolean result = true;
		for (T entry : entrys) {
			result &= returnResult(collection.remove(DBObjectUtil.convertPO2DBObject(entry)));
		}
		return result;
	}

	public <T> Page<T> query(Query query) {
		Assert.assertNotNull(query);
		DBCollection collection = getCollection(query.getQueryPOClass());
		DBCursor cursor = collection.find(query.buildQuery());
		if (query.getOrderMap() != null) {
			cursor.sort(query.getOrderMap());
		}
		if (query.getMax() > 0) {
			cursor.limit(query.getMax());
		} else if (query.getPageSize() > 0) {
			cursor.skip(query.getPageNum() * query.getPageSize());
		}
		if (query.getProjection() != null) {
			cursor.addSpecial("$returnKey", query.getProjection());
		}
		return fillPage(query, cursor);
	}

	public <T> boolean update(Query query, T entry) {
		return update(query, entry, true, true);
	}

	public <T> boolean update(Query query, T entry, boolean upsert, boolean multi) {
		Assert.assertNotNull(query);
		DBCollection collection = getCollection(query.getQueryPOClass());
		return returnResult(collection.update(query.buildQuery(), new BasicDBObject("$set", DBObjectUtil.convertPO2DBObject(entry)), upsert, multi));
	}

	@SuppressWarnings("unchecked")
	private <T> Page<T> fillPage(Query query, DBCursor cursor) {
		Page<T> page = new Page<T>();
		page.setTotalCount(cursor.count());
		page.setPageNum(query.getPageNum());
		page.setPageSize(query.getPageSize());
		List<T> results = new ArrayList<T>();
		for (DBObject object : cursor) {
			try {
				results.add(DBObjectUtil.fillDocument2PO(object, (T) query.getQueryPOClass().newInstance()));
			} catch (InstantiationException e) {
				e.printStackTrace();
				throw new MongoDataAccessException("can't instance a interface or abstract class", e);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				throw new MongoDataAccessException(e);
			}
		}
		page.setResults(results);
		return page;
	}
	private boolean returnResult(WriteResult result) {
		if (result == null || StringUtils.hasText(result.getError())) {
			return false;
		}
		return true;
	}
}
