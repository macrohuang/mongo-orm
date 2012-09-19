package github.macrohuang.orm.mongo.query;

import github.macrohuang.orm.mongo.config.DBChooser;
import github.macrohuang.orm.mongo.core.Assert;
import github.macrohuang.orm.mongo.util.FieldCacheMap;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class Query {
	private Class<?> class1;
	private DBObject queryObject;
	private DBObject orderMap;
	private int skip;
	private int pageSize;
	private int pageNum;
	private int max;
	private DBObject projection;
	private List<String> groups;
	private List<DBChooser> dbChoosers;
	private boolean usingDBChooser;

	public Query(Class<?> class1) {
		this.class1 = class1;
		queryObject = new BasicDBObject();
		FieldCacheMap.getInstance().addIfAbsent(class1);
	}

	public Class<?> getQueryPOClass() {
		return class1;
	}

	private String getDocKey(String field) {
		String docKey = FieldCacheMap.getInstance().getFieldDocKey(getQueryPOClass(), field);
		if (FieldCacheMap.getInstance().isEmbed(getQueryPOClass(), field)) {
			return FieldCacheMap.getInstance().getEmbedParent(getQueryPOClass(), field) + "." + docKey;
		} else {
			return docKey;
		}
	}
	// It now works!!!
	public Query include(String field){
		if (projection == null)
			projection = new BasicDBObject();
		projection.put(getDocKey(field), 1);
		return this;
	}

	public Query addCondition(String field, QueryOperators operators, Object value) {
		if (operators == QueryOperators.EQ) {
			queryObject.put(getDocKey(field), value);
		} else {
			if (queryObject.get(getDocKey(field)) != null) {
				((BasicDBObject) queryObject.get(getDocKey(field))).put(operators.getOperate(), value);
			} else {
				queryObject.put(getDocKey(field), new BasicDBObject(operators.getOperate(), value));
			}
		}
		return this;
	}

	public Query and(String field, QueryOperators operators, Object value) {
		return addCondition(field, operators, value);
	}

	public Query or(String field, QueryOperators operators, Object value) {
		if (!queryObject.containsField("$or")) {
			queryObject.put("$or", new BasicDBList());
		}
		BasicDBList object = (BasicDBList) queryObject.get("$or");
		object.add(new BasicDBObject(getDocKey(field), value));
		return this;
	}

	public Query addOrder(String field, Order order) {
		if (orderMap == null)
			orderMap = new BasicDBObject();
		orderMap.put(getDocKey(field), order.getOrder());
		return this;
	}

	public DBObject getProjection() {
		return projection;
	}

	public DBObject getOrderMap() {
		return orderMap;
	}

	public DBObject buildQuery() {
		return queryObject;
	}

	public int getSkip() {
		return skip;
	}

	public int getPageSize() {
		return pageSize;
	}

	public int getPageNum() {
		return pageNum;
	}

	public int getMax() {
		return max;
	}

	public Query skip(int skip) {
		if (skip > 0) {
			this.skip = skip;
		}
		return this;
	}

	public Query pageSize(int pageSize) {
		if (pageSize > 0) {
			this.pageSize = pageSize;
		}
		return this;
	}

	public Query max(int max) {
		if (max > 0) {
			this.max = max;
		}
		return this;
	}

	/**
	 * page number, start with 0.
	 * 
	 * @param pageNum
	 * @return
	 */
	public Query pageNum(int pageNum) {
		if (pageNum > -1) {
			this.pageNum = pageNum;
		}
		return this;
	}

	public Query clearPagingInfo() {
		this.pageNum = 0;
		this.pageSize = -1;
		this.max = -1;
		return this;
	}
	public Query groupBy(String field) {
		if (groups == null) {
			groups = new ArrayList<String>();
		}
		groups.add(field);
		return this;
	}

	public List<String> getGroups() {
		return groups;
	}

	public Query addDBChooser(DBChooser dbChooser) {
		Assert.assertNotNull(dbChooser);
		if (dbChoosers == null) {
			usingDBChooser = true;
			dbChoosers = new ArrayList<DBChooser>();
		}
		if (!dbChoosers.contains(dbChooser)) {
			dbChoosers.add(dbChooser);
		}
		return this;
	}

	public List<DBChooser> getDbChoosers() {
		return dbChoosers;
	}

	public boolean isUsingDBChooser() {
		return usingDBChooser;
	}

	public Object getQueryCondition(String field) {
		if (queryObject.containsField(getDocKey(field))) {
			return queryObject.get(getDocKey(field));
		} else {
			return null;
		}
	}
	@Override
	public String toString() {
		return "Query [class1=" + class1 + ", queryObject=" + queryObject + ", orderMap=" + orderMap + ", skip=" + skip + ", pageSize=" + pageSize
				+ ", pageNum=" + pageNum + ", max=" + max + ", projection=" + projection + "]";
	}

}
