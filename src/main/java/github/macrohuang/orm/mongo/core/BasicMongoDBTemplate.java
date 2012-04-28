package github.macrohuang.orm.mongo.core;

import github.macrohuang.orm.mongo.annotation.Document;
import github.macrohuang.orm.mongo.annotation.MongoField;
import github.macrohuang.orm.mongo.config.DBChooser;
import github.macrohuang.orm.mongo.exception.MongoDataAccessException;
import github.macrohuang.orm.mongo.factory.MongoDBFactory;
import github.macrohuang.orm.mongo.query.Page;
import github.macrohuang.orm.mongo.query.Query;
import github.macrohuang.orm.mongo.util.DBObjectUtil;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

public class BasicMongoDBTemplate {
	private MongoDBFactory dbFactory;
	protected int batchSize = 1000;
	private static final Logger LOGGER = Logger.getLogger(BasicMongoDBTemplate.class);


	/**
	 * Delete documents match the given <code>entry</code>. Note: this entry
	 * class <b>must</b> be annotated with <code>@Document</code> annotation, or
	 * an <code>MongoDataAccessException</code> will be thrown.
	 * 
	 * @param <T>
	 * @param entry
	 *            The document sample to be deleted. Any documents match this
	 *            entry query will be deleted.
	 * @return <code>true</code> if the deletion is success, or
	 *         <code>false</code> while fail.
	 * @throws MongoDataAccessException
	 */
	public <T> boolean delete(T entry) throws MongoDataAccessException {
		Assert.assertNotNull(entry);
		LOGGER.info("delete request received: " + entry);
		DBCollection collection = getCollection(entry);
		boolean result = returnResult(collection.remove(DBObjectUtil.convertPO2DBObject(entry)));
		LOGGER.info("delete result: " + result);
		return result;
	}

	/**
	 * Delete documents match any of the given <code>entries</code>. Note: this
	 * entry class <b>must</b> be annotated with <code>@Document</code>
	 * annotation, or an <code>MongoDataAccessException</code> will be thrown.
	 * 
	 * @param <T>
	 * @param entrys
	 *            Collection of template.
	 * @return <code>true</code> if the deletion is success, or
	 *         <code>false</code> while any fail.
	 * @throws MongoDataAccessException
	 */
	public <T> boolean deleteAll(List<T> entrys) throws MongoDataAccessException {
		Assert.assertNotNull(entrys);
		Assert.assertNotEmpty(entrys);
		return deleteAllInner(entrys, getCollection(entrys.get(0)));
	}

	protected <T> boolean deleteAllInner(List<T> entrys, DBCollection collection) {
		LOGGER.info("delete all request received:" + entrys + "," + collection);
		boolean result = true;
		for (T entry : entrys) {
			result &= returnResult(collection.remove(DBObjectUtil.convertPO2DBObject(entry)));
		}
		LOGGER.info("delete result:" + result);
		return result;
	}

	@SuppressWarnings("unchecked")
	protected <T> Page<T> fillPage(Query query, DBCursor cursor) {
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

	/**
	 * Query by example. <b>Note:</b> the class of given entry must be annotated
	 * by an <code>@Document</code> annotation, or a
	 * <code>MongoDataAccessException</code> will be thrown. Also, the query
	 * field must be annotated by an <code>@MongoField</code> annotation. If the
	 * <code>field</code> of this annotation is specified, then it'll be used as
	 * the MongoDB document field, otherwise, the lower case of the field name
	 * of this entry will be used.
	 * 
	 * @see MongoField
	 * @see Document
	 * @param <T>
	 * @param entry
	 *            The query template, any null fields will be omitted.
	 * @return The match documents or an empty <code>List</code> for no match.
	 * @throws MongoDataAccessException
	 */
	public <T> List<T> findByExample(T entry) throws MongoDataAccessException {
		Assert.assertNotNull(entry);
		LOGGER.info("Find by example request receive:" + entry);
		return findByExampleInner(getCollection(entry), entry);
	}

	/**
	 * Query the total count by example.
	 * 
	 * @see MongoDBTemplate#findByExample(Object)
	 * @param <T>
	 * @param entry
	 * @return The total count match the query.
	 * @throws MongoDataAccessException
	 */
	public <T> int getCountByExample(T entry) throws MongoDataAccessException {
		Assert.assertNotNull(entry);
		LOGGER.info("getCountByExample request receive:" + entry);
		DBCursor dbCursor = getCollection(entry).find(DBObjectUtil.convertPO2DBObject(entry));
		return dbCursor == null ? 0 : dbCursor.count();
	}

	@SuppressWarnings("unchecked")
	protected <T> List<T> findByExampleInner(DBCollection collection, T entry) {
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

	public int getBatchSize() {
		return batchSize;
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

	public MongoDBFactory getDbFactory() {
		return dbFactory;
	}

	/**
	 * Query with a query object. The <code>query</code> must be given a class
	 * of a PO Object with
	 * {@link com.sogou.bizdev.mongo.orm.annotation.Document} annotation.
	 * 
	 * @see MongoDBTemplate#findByExample(Object)
	 * @see com.sogou.bizdev.mongo.orm.annotation.Document
	 * @param <T>
	 * @param query
	 * @return A page instance fill with the query result, including the
	 *         pageSize, pageNum, totalCount, resultList.
	 */
	public <T> Page<T> query(Query query) {
		Assert.assertNotNull(query);
		LOGGER.info("query receive:" + query);
		return queryInner(getCollection(query.getQueryPOClass()), query);
	}

	/**
	 * Get the result count of given query.
	 * 
	 * @see MongoDBTemplate#getCountByExample(Object)
	 * @param <T>
	 * @param query
	 * @return
	 */
	public <T> int getCount(Query query) {
		Assert.assertNotNull(query);
		return getCountInner(getCollection(query.getQueryPOClass()), query);
	}


	protected <T> int getCountInner(DBCollection collection, Query query) {
		DBCursor dbCursor = null;
		if (query.getProjection() != null) {
			dbCursor = collection.find(query.buildQuery(), query.getProjection());
		} else {
			dbCursor = collection.find(query.buildQuery());
		}
		return dbCursor == null ? 0 : dbCursor.count();
	}

	protected <T> Page<T> queryInner(DBCollection collection, Query query) {
		DBCursor cursor = null;
		if (query.getProjection() != null) {
			cursor = collection.find(query.buildQuery(), query.getProjection());
		} else {
			cursor = collection.find(query.buildQuery());
		}
		if (query.getOrderMap() != null) {
			cursor.sort(query.getOrderMap());
		}
		if (query.getMax() > 0) {
			cursor.limit(query.getMax());
		} else if (query.getPageSize() > 0) {
			cursor.skip(query.getPageNum() * query.getPageSize());
		}
		return fillPage(query, cursor);
	}

	protected boolean returnResult(WriteResult result) {
		if (result == null || StringUtils.hasText(result.getError())) {
			return false;
		}
		return true;
	}


	/**
	 * Save an entry to the DB.<b>Note:</b> the class of given entry must be
	 * annotated by a {@link com.sogou.bizdev.mongo.orm.annotation.Document}
	 * annotation, and only those fields of the given class annotated by
	 * {@link com.sogou.bizdev.mongo.orm.annotation.MongoField} will be mapped
	 * to the collection.
	 * 
	 * @param <T>
	 * @param entry
	 *            An POJO annotated by a
	 *            {@link com.sogou.bizdev.mongo.orm.annotation.Document} and
	 *            some {@link com.sogou.bizdev.mongo.orm.annotation.MongoField}
	 *            .
	 * @return The result of save action.<code>true</code> for success, while
	 *         <code>false</code> for fail.
	 * @throws MongoDataAccessException
	 */
	public <T> boolean save(T entry) throws MongoDataAccessException {
		Assert.assertNotNull(entry);
		LOGGER.info("Save request received:" + entry);
		DBCollection collection = getCollection(entry);
		return returnResult(collection.insert(DBObjectUtil.convertPO2DBObject(entry)));
	}

	/**
	 * Save a lot of entries. {@link #save(Object)}
	 * 
	 * @see #save(Object)
	 * @param <T>
	 * @param entrys
	 * @return
	 * @throws MongoDataAccessException
	 */
	public <T> boolean saveAll(List<T> entrys) throws MongoDataAccessException {
		Assert.assertNotNull(entrys);
		Assert.assertNotEmpty(entrys);
		LOGGER.info("saveAll request received:" + entrys);
		return saveAllInner(getCollection(entrys.get(0)), entrys);
	}

	protected <T> boolean saveAllInner(DBCollection collection, List<T> entrys) {
		List<DBObject> list = new ArrayList<DBObject>();
		for (T entry : entrys) {
			list.add(DBObjectUtil.convertPO2DBObject(entry));
		}
		return returnResult(collection.insert(list));
	}

	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	public void setDbFactory(MongoDBFactory dbFactory) {
		this.dbFactory = dbFactory;
	}

	/**
	 * {@link #update(DBChooser, Query, Object)}
	 * 
	 * @see #update(DBChooser, Query, Object)
	 * @param <T>
	 * @param query
	 * @param entry
	 * @return
	 */
	public <T> boolean update(Query query, T entry) {
		Assert.assertNotNull(query);
		Assert.assertNotNull(entry);
		LOGGER.info("update:" + ",query:" + query + ",entry:" + entry);
		return update(query, entry, true, true);
	}

	/**
	 * {@link #update(DBChooser, Query, Object, boolean, boolean)}
	 * 
	 * @see #update(DBChooser, Query, Object, boolean, boolean)
	 * @param <T>
	 * @param query
	 * @param entry
	 * @param upsert
	 * @param multi
	 * @return
	 */
	public <T> boolean update(Query query, T entry, boolean upsert, boolean multi) {
		Assert.assertNotNull(query);
		Assert.assertNotNull(entry);
		LOGGER.info("update:" + ",query:" + query + ",entry:" + entry + ",upsert:" + upsert + ",multi:" + multi);
		DBCollection collection = getCollection(query.getQueryPOClass());
		return returnResult(collection.update(query.buildQuery(), new BasicDBObject("$set", DBObjectUtil.convertPO2DBObject(entry)), upsert, multi));
	}

}
