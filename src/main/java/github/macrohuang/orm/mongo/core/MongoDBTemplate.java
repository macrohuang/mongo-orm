package github.macrohuang.orm.mongo.core;
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


import github.macrohuang.orm.mongo.config.DBChooser;
import github.macrohuang.orm.mongo.constant.Constants;
import github.macrohuang.orm.mongo.exception.MongoDataAccessException;
import github.macrohuang.orm.mongo.query.Page;
import github.macrohuang.orm.mongo.query.Query;
import github.macrohuang.orm.mongo.util.DBObjectUtil;

import java.util.List;

import org.apache.log4j.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

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
public class MongoDBTemplate extends BasicMongoDBTemplate {
	private static final Logger LOGGER = Logger.getLogger(MongoDBTemplate.class);

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
		LOGGER.info("delete request received: " + dbChooser + entry);
		DBCollection collection = getCollection(dbChooser);
		boolean result = returnResult(collection.remove(DBObjectUtil.convertPO2DBObject(entry)));
		LOGGER.info("delete result: " + result);
		return result;
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
		LOGGER.info("Find by example request receive:" + dbChooser + "," + entry);
		return findByExampleInner(getCollection(dbChooser), entry);
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
		LOGGER.info("query receive:" + dbChooser + "," + query);
		return queryInner(getCollection(dbChooser), query);
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
		return getCountInner(getCollection(chooser), query);
	}

	/**
	 * Save an entry to the DB, with manual specify {@link DBChooser}.
	 * 
	 * @see #insert(Object)
	 * @see #findByExample(DBChooser, Object)
	 * @param <T>
	 * @param dbChooser
	 * @param entry
	 * @return The result of save action.<code>true</code> for success, while
	 *         <code>false</code> for fail.
	 * @throws MongoDataAccessException
	 */
	public <T> String insert(DBChooser dbChooser, T entry) throws MongoDataAccessException {
		Assert.assertNotNull(entry);
		Assert.assertNotNull(dbChooser);
		LOGGER.info("Save request received:" + dbChooser + "," + entry);
		DBCollection collection = getCollection(dbChooser);
		DBObject object = DBObjectUtil.convertPO2DBObject(entry);
		if (returnResult(collection.insert(object))) {
			DBObjectUtil.setEntryId(object, entry);
			return object.get(Constants.MONGO_ID).toString();
		} else {
			return null;
		}
	}

	public <T> String saveOrUpdate(DBChooser dbChooser, T entry) throws MongoDataAccessException {
		Assert.assertNotNull(dbChooser);
		Assert.assertNotNull(entry);
		LOGGER.info("Save request received:" + entry);
		DBCollection collection = getCollection(dbChooser);
		DBObject po = DBObjectUtil.convertPO2DBObject(entry);
		if (returnResult(collection.save(po))) {
			DBObjectUtil.setEntryId(po, entry);
			return po.get(Constants.MONGO_ID).toString();
		} else {
			return null;
		}
	}

	/**
	 * Save a lot of entries with manual specify DBChooser.
	 * {@link #insert(DBChooser, Object)}
	 * 
	 * @see #insert(DBChooser, Object)
	 * @param <T>
	 * @param dbChooser
	 * @param entrys
	 * @return
	 * @throws MongoDataAccessException
	 */
	public <T> boolean insertAll(DBChooser dbChooser, List<T> entrys) throws MongoDataAccessException {
		Assert.assertNotNull(entrys);
		Assert.assertNotNull(dbChooser);
		Assert.assertNotEmpty(entrys);
		LOGGER.info("saveAll request received:" + dbChooser + "," + entrys);
		return insertAllInner(getCollection(dbChooser), entrys);
	}

	public <T> boolean saveOrUpdateAll(DBChooser dbChooser, List<T> entrys) throws MongoDataAccessException {
		Assert.assertNotNull(entrys);
		Assert.assertNotEmpty(entrys);
		LOGGER.info("saveOrUpdateAll request received:" + entrys);
		return saveOrUpdateAllInner(getCollection(dbChooser), entrys);
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
		LOGGER.info("update:" + dbChooser + "," + query + "," + entry);
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
		LOGGER.info("update:" + dbChooser + ",query:" + query + ",entry:" + entry + ",upsert:" + upsert + ",multi:" + multi);
		DBCollection collection = getCollection(dbChooser);
		return returnResult(collection.update(query.buildQuery(), new BasicDBObject("$set", DBObjectUtil.convertPO2DBObject(entry)), upsert, multi));
	}
}
