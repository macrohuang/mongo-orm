package github.macrohuang.orm.mongo.query;

import github.macrohuang.orm.mongo.annotation.MongoField;
import github.macrohuang.orm.mongo.util.DBObjectUtil;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class Query {
	private Class<?> class1;
	private DBObject queryObject;
	private DBObject orderMap;
	private static Map<String, Map<String, String>> poField2DocumentMap = new HashMap<String, Map<String, String>>();
	private int skip;
	private int pageSize;
	private int pageNum;
	private int max;
	private DBObject projection;

	public Query(Class<?> class1) {
		this.class1 = class1;
		queryObject = new BasicDBObject();
		if (!poField2DocumentMap.containsKey(class1.getName())) {
			Map<String, String> fieldMap = new HashMap<String, String>();
			for (Field field : class1.getDeclaredFields()) {
				if (field.getAnnotation(MongoField.class) != null) {
					fieldMap.put(field.getName(), DBObjectUtil.getMongoField(field));
				}
			}
			poField2DocumentMap.put(class1.getName(), fieldMap);
		}
	}

	public Class<?> getQueryPOClass() {
		return class1;
	}

	// It now works!!!
	public Query include(String field){
		if (projection == null)
			projection = new BasicDBObject();
		projection.put(field, 1);
		return this;
	}
	public Query addCondition(String field, QueryOperators operators, Object value) {
		if (operators == QueryOperators.EQ) {
			queryObject.put(poField2DocumentMap.get(class1.getName()).get(field), value);
		} else {
			if (queryObject.get(getDocumentField(field)) != null) {
				((BasicDBObject) queryObject.get(getDocumentField(field))).put(operators.getOperate(), value);
			} else {
				queryObject.put(getDocumentField(field), new BasicDBObject(operators.getOperate(), value));
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
		object.add(new BasicDBObject(getDocumentField(field), value));
		return this;
	}

	private String getDocumentField(String field) {
	    return poField2DocumentMap.get(class1.getName()).get(field);
    }

	public Query addOrder(String field, Order order) {
		if (orderMap == null)
			orderMap = new BasicDBObject();
		orderMap.put(poField2DocumentMap.get(class1.getName()).get(field), order.getOrder());
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

	@Override
	public String toString() {
		return "Query [class1=" + class1 + ", queryObject=" + queryObject + ", orderMap=" + orderMap + ", skip=" + skip + ", pageSize=" + pageSize
				+ ", pageNum=" + pageNum + ", max=" + max + ", projection=" + projection + "]";
	}

}
