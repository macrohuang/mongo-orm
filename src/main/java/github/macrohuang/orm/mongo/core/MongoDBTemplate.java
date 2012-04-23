/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
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

import org.springframework.util.StringUtils;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

/**
 * <p>
 * This class supply the most useful operation for working with MongoDB.
 * </p>
 * It supplies two ways to communicate with MongoDB java driver,
 * <ul>
 * <li>Any class annotation with<code> @Document</code>, it should be given a
 * <code>db</code> name, with the optional of <code>collection</code> name.<br>
 * If the <code>collection</code> name doesn't declare, it'll try to find a
 * collection equals to the base name of the <code>class</code>, in lower case.</li>
 * <li>If the class doesn't annotation with <code>@Document</code>, then you
 * must supply a <code>DBChooser</code> where you specify the <code>DB</code>
 * and the <code>DBCollection</code> manual instead.</li>
 * </ul>
 * 
 * 
 * @author huangtianlai
 * 
 */
public class MongoDBTemplate {
	private MongoDBFactory dbFactory;
	private int batchSize = 1000;

	/**
	 * Delete documents match the given <code>entry</code> with manual specify
	 * <code>DBChooser</code>
	 * 
	 * @param <T>
	 * @param dbChooser
	 *            The <code>DBChoose</code> configure with the DB name and
	 *            collection name.
	 * @param entry
	 *            The document to be deleted. Any documents match this entry
	 *            query will be deleted.
	 * @return <code>true</code> if the deletion is success, or
	 *         <code>false</code> while fail.
	 * @throws MongoDataAccessException
	 */
	public <T> boolean delete(DBChooser dbChooser, T entry) throws MongoDataAccessException {
		Assert.assertNotNull(entry);
		DBCollection collection = getCollection(dbChooser);
		return returnResult(collection.remove(DBObjectUtil.convertPO2DBObject(entry)));
	}

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
		DBCollection collection = getCollection(entry);
		return returnResult(collection.remove(DBObjectUtil.convertPO2DBObject(entry)));
	}

	/**
	 * Delete documents match any of the given <code>entries</code> with manual
	 * specify <code>DBChooser</code>
	 * 
	 * @param <T>
	 * @param dbChooser
	 *            The <code>DBChoose</code> configure with the DB name and
	 *            collection name.
	 * @param entries
	 *            Collection of template.
	 * @return <code>true</code> if the deletion is success, or
	 *         <code>false</code> while any fail.
	 * @throws MongoDataAccessException
	 */
	public <T> boolean deleteAll(DBChooser dbChooser, List<T> entries) throws MongoDataAccessException {
		Assert.assertNotNull(entries);
		Assert.assertNotEmpty(entries);
		return deleteAllInner(entries, getCollection(dbChooser));
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

	private <T> boolean deleteAllInner(List<T> entrys, DBCollection collection) {
		boolean result = true;
		for (T entry : entrys) {
			result &= returnResult(collection.remove(DBObjectUtil.convertPO2DBObject(entry)));
		}
		return result;
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

	/**
	 * Query by example with the manual specify <code>DB</code> and
	 * <code>DBCollection</code>.
	 * 
	 * @param <T>
	 * @param dbChooser
	 *            The <code>DBChoose</code> configure with the DB name and
	 *            collection name.
	 * @param entry
	 *            The query template, any null fields will be omitted.
	 * @return The match documents or an empty <code>List</code> for no match.
	 * @throws MongoDataAccessException
	 */
	public <T> List<T> findByExample(DBChooser dbChooser, T entry) throws MongoDataAccessException {
		Assert.assertNotNull(entry);
		Assert.assertNotNull(dbChooser);
		return findByExampleInner(getCollection(dbChooser), entry);
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
		DBCursor dbCursor = getCollection(entry).find(DBObjectUtil.convertPO2DBObject(entry));
		return dbCursor == null ? 0 : dbCursor.count();
	}

	/**
	 * Query the total count by example with manual specify DBChooser.
	 * 
	 * @see MongoDBTemplate#findByExample(DBChooser, Object)
	 * @param <T>
	 * @param chooser
	 * @param entry
	 * @return
	 * @throws MongoDataAccessException
	 */
	public <T> int getCountByExample(DBChooser chooser, T entry) throws MongoDataAccessException {
		Assert.assertNotNull(entry);
		Assert.assertNotNull(chooser);
		DBCursor dbCursor = getCollection(chooser).find(DBObjectUtil.convertPO2DBObject(entry));
		return dbCursor == null ? 0 : dbCursor.count();
	}

	@SuppressWarnings("unchecked")
	private <T> List<T> findByExampleInner(DBCollection collection, T entry) {
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

	private <T> DBCollection getCollection(DBChooser dbChooser) throws MongoDataAccessException {
		if (dbChooser == null)
			throw new MongoDataAccessException("DBChooser can not be null");
		return dbFactory.getDB(dbChooser.getDb()).getCollection(dbChooser.getCollection());
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
	 * Query with a query object and manual specify DBChooser.
	 * 
	 * @see Query
	 * @see Page
	 * @param <T>
	 * @param dbChooser
	 * @param query
	 * @return A page instance fill with the query result, including the
	 *         pageSize, pageNum, totalCount, resultList.
	 */
	public <T> Page<T> query(DBChooser dbChooser, Query query) {
		Assert.assertNotNull(query);
		Assert.assertNotNull(dbChooser);
		return queryInner(getCollection(dbChooser), query);
	}

	/**
	 * Query with a query object. The <code>query</code> must be given a class
	 * of a PO Object with
	 * {@link github.macrohuang.orm.mongo.annotation.Document} annotation.
	 * 
	 * @see MongoDBTemplate#findByExample(Object)
	 * @see github.macrohuang.orm.mongo.annotation.Document
	 * @param <T>
	 * @param query
	 * @return A page instance fill with the query result, including the
	 *         pageSize, pageNum, totalCount, resultList.
	 */
	public <T> Page<T> query(Query query) {
		Assert.assertNotNull(query);
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
		DBCursor cursor = getCollection(query.getQueryPOClass()).find(query.buildQuery());
		return (cursor == null ? 0 : cursor.count());
	}

	/**
	 * Get the result count of given query via manual specify {@link DBChooser }.
	 * 
	 * @see MongoDBTemplate#getCountByExample(DBChooser, Object)
	 * @param <T>
	 * @param chooser
	 * @param query
	 * @return
	 */
	public <T> int getCount(DBChooser chooser, Query query) {
		Assert.assertNotNull(query);
		Assert.assertNotNull(chooser);
		DBCursor cursor = getCollection(chooser).find(query.buildQuery());
		return (cursor == null ? 0 : cursor.count());
	}

	private <T> Page<T> queryInner(DBCollection collection, Query query) {
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

	private boolean returnResult(WriteResult result) {
		if (result == null || StringUtils.hasText(result.getError())) {
			return false;
		}
		return true;
	}

	/**
	 * Save an entry to the DB, with manual specify {@link DBChooser}.
	 * 
	 * @see #save(Object)
	 * @see #findByExample(DBChooser, Object)
	 * @param <T>
	 * @param dbChooser
	 * @param entry
	 * @return The result of save action.<code>true</code> for success, while
	 *         <code>false</code> for fail.
	 * @throws MongoDataAccessException
	 */
	public <T> boolean save(DBChooser dbChooser, T entry) throws MongoDataAccessException {
		Assert.assertNotNull(entry);
		Assert.assertNotNull(dbChooser);
		DBCollection collection = getCollection(dbChooser);
		return returnResult(collection.insert(DBObjectUtil.convertPO2DBObject(entry)));
	}

	/**
	 * Save an entry to the DB.<b>Note:</b> the class of given entry must be
	 * annotated by a {@link github.macrohuang.orm.mongo.annotation.Document}
	 * annotation, and only those fields of the given class annotated by
	 * {@link github.macrohuang.orm.mongo.annotation.MongoField} will be mapped
	 * to the collection.
	 * 
	 * @param <T>
	 * @param entry
	 *            An POJO annotated by a
	 *            {@link github.macrohuang.orm.mongo.annotation.Document} and
	 *            some {@link github.macrohuang.orm.mongo.annotation.MongoField}
	 *            .
	 * @return The result of save action.<code>true</code> for success, while
	 *         <code>false</code> for fail.
	 * @throws MongoDataAccessException
	 */
	public <T> boolean save(T entry) throws MongoDataAccessException {
		Assert.assertNotNull(entry);
		DBCollection collection = getCollection(entry);
		return returnResult(collection.insert(DBObjectUtil.convertPO2DBObject(entry)));
	}

	/**
	 * Save a lot of entries with manual specify DBChooser.
	 * {@link #save(DBChooser, Object)}
	 * 
	 * @see #save(DBChooser, Object)
	 * @param <T>
	 * @param dbChooser
	 * @param entrys
	 * @return
	 * @throws MongoDataAccessException
	 */
	public <T> boolean saveAll(DBChooser dbChooser, List<T> entrys) throws MongoDataAccessException {
		Assert.assertNotNull(entrys);
		Assert.assertNotNull(dbChooser);
		Assert.assertNotEmpty(entrys);
		return saveAllInner(getCollection(dbChooser), entrys);
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
		return saveAllInner(getCollection(entrys.get(0)), entrys);
	}

	private <T> boolean saveAllInner(DBCollection collection, List<T> entrys) {
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
	 * Update all the match documents, set their values to the same as the given
	 * entry. Note that, the <code>null</code> value of the entry is omitted.
	 * This will default set the upsert and multi to true,
	 * {@link #update(DBChooser, Query, Object, boolean, boolean)}.
	 * 
	 * @see #update(DBChooser, Query, Object, boolean, boolean)
	 * @see DBCollection#update(DBObject, DBObject, boolean, boolean)
	 * 
	 * @param <T>
	 * @param dbChooser
	 * @param query
	 * @param entry
	 * @return
	 */
	public <T> boolean update(DBChooser dbChooser, Query query, T entry) {
		Assert.assertNotNull(dbChooser);
		Assert.assertNotNull(query);
		Assert.assertNotNull(entry);
		return update(dbChooser, query, entry, true, true);
	}

	/**
	 * Update all the match documents, set their values to the same as the given
	 * entry. Note that, the <code>null</code> value of the entry is omitted.
	 * You can manual specify the upsert and the multi value.
	 * 
	 * @see DBCollection#update(DBObject, DBObject, boolean, boolean)
	 * @param <T>
	 * @param dbChooser
	 * @param query
	 * @param entry
	 * @param upsert
	 * @param multi
	 * @return
	 */
	public <T> boolean update(DBChooser dbChooser, Query query, T entry, boolean upsert, boolean multi) {
		Assert.assertNotNull(query);
		Assert.assertNotNull(dbChooser);
		DBCollection collection = getCollection(dbChooser);
		return returnResult(collection.update(query.buildQuery(), new BasicDBObject("$set", DBObjectUtil.convertPO2DBObject(entry)), upsert, multi));
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
		DBCollection collection = getCollection(query.getQueryPOClass());
		return returnResult(collection.update(query.buildQuery(), new BasicDBObject("$set", DBObjectUtil.convertPO2DBObject(entry)), upsert, multi));
	}
}
