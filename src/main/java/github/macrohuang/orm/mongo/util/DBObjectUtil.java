package github.macrohuang.orm.mongo.util;


import github.macrohuang.orm.mongo.annotation.Document;
import github.macrohuang.orm.mongo.annotation.MongoField;
import github.macrohuang.orm.mongo.exception.MongoDBMappingException;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class DBObjectUtil {
	private static final Map<String, Map<String, Field>> FIELD_CACHE_MAP = new ConcurrentHashMap<String, Map<String, Field>>();
	private static final Logger logger = Logger.getLogger(DBObjectUtil.class);

	public static DBObject convertPO2DBObject(Object object) throws MongoDBMappingException {
		logger.info("convert " + object + " into DBObject");
		return convertPO2DBObject(object, 1);
	}

	public static DBObject convertPO2DBObject(Object object, int deepth) throws MongoDBMappingException {
		logger.info("convert " + object + " into DBObject with deepth: " + deepth);
		return convertPO2DBObjectInner(object, false, deepth, 0);
	}

	public static DBObject convertPO2DBObject(Object object, boolean nullable, int deepth) throws MongoDBMappingException {
		logger.info("convert " + object + " into DBObject with deepth: " + deepth + ", enable null value: " + nullable);
		return convertPO2DBObjectInner(object, nullable, deepth, 0);
	}

	public static Method getFieldGetterMethod(Object object, Field field) throws SecurityException, NoSuchMethodException {
		StringBuilder getterMethod = new StringBuilder();
		getterMethod.append("get").append(("" + field.getName().charAt(0)).toUpperCase()).append(field.getName().substring(1));
		return object.getClass().getMethod(getterMethod.toString(), new Class[0]);
	}

	public static String getMongoField(Field field) {
		MongoField mongoField = field.getAnnotation(MongoField.class);
		return StringUtils.hasText(mongoField.field()) ? mongoField.field() : field.getName();
	}

	private static DBObject convertPO2DBObjectInner(Object object, boolean nullable, int deepth, int currentDepth) throws MongoDBMappingException {
		if (object == null || currentDepth == deepth) {
			return new BasicDBObject();
		}
		Map<String, Field> fields = getFieldMap(object);
		DBObject dbObject;
		if (object instanceof Collection) {
			dbObject = new BasicDBList();
		} else {
			dbObject = new BasicDBObject();
		}
		Field field;
		for (String docKey : fields.keySet()) {
			field = fields.get(docKey);
			field.setAccessible(true);
			try {
				if (field.getType().getAnnotation(Document.class) != null) {
					dbObject.put(docKey, convertPO2DBObjectInner(field.get(object), nullable, deepth, currentDepth + 1));
				} else {
					if (nullable || !nullable && field.get(object) != null) {
						dbObject.put(docKey, field.get(object));
					}
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				throw new MongoDBMappingException("getter should not have any argument", e);
			} catch (SecurityException e) {
				e.printStackTrace();
				throw new MongoDBMappingException("you don't have permission to access this method", e);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				throw new MongoDBMappingException("you don't have permission to access this field, properly there doesn't a getter exists.", e);
			}
		}
		logger.info("Convert resutl:" + dbObject);
		return dbObject;
	}

	public static Method getFieldSetterMethod(Object object, Field field) throws SecurityException, NoSuchMethodException {
		StringBuilder getterMethod = new StringBuilder();
		getterMethod.append("set").append(("" + field.getName().charAt(0)).toUpperCase()).append(field.getName().substring(1));
		return object.getClass().getMethod(getterMethod.toString(), field.getType());
	}

	private static <T> Map<String, Field> getFieldMap(T po) {
		Map<String, Field> fields = FIELD_CACHE_MAP.get(po.getClass().getName());
		if (fields == null) {
			fields = new HashMap<String, Field>();
			for (Field field : po.getClass().getDeclaredFields()) {
				if (field.getAnnotation(MongoField.class) != null) {
					fields.put(getMongoField(field), field);
				}
			}
			FIELD_CACHE_MAP.put(po.getClass().getName(), fields);
		}
		return fields;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T fillDocument2PO(DBObject dbObject, T po) {
		if (po == null)
			throw new MongoDBMappingException("can't not fill a document into a null po.");
		Map<String, Field> fields = getFieldMap(po);
		Field field;
		for (String mongoField : dbObject.keySet()) {
			if (fields.containsKey(mongoField)) {
				field = fields.get(mongoField);
				try {
					field.setAccessible(true);
					Object docVal = dbObject.get(mongoField);
					if (docVal instanceof DBObject) {
						if (field.getType().getAnnotation(Document.class) != null) {
							field.set(po, fillDocument2PO((DBObject) docVal, (T) field.getType().newInstance()));
						} else {
							if (docVal instanceof BasicDBList) {
								if (field.getType().isArray()) {
									Object[] objects = (Object[]) Array
											.newInstance(field.getType().getComponentType(), ((BasicDBList) docVal).size());

									field.set(po, ((BasicDBList) docVal).toArray(objects));
								} else if (docVal instanceof Collection) {
									if (field.getType() == Set.class) {
										Set set = new HashSet();
										set.addAll((Collection) docVal);
										field.set(po, set);
									} else if (field.getType() == List.class) {
										List list = new ArrayList();
										list.addAll((Collection) docVal);
										field.set(po, list);
									}
								} else {
									field.set(po, docVal);
								}
							}
						}
					} else {
						BeanUtils.setProperty(po, field.getName(), docVal);
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
					throw new MongoDBMappingException("setter argument does match", e);
				} catch (SecurityException e) {
					e.printStackTrace();
					throw new MongoDBMappingException("you don't have permission to access this method", e);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					throw new MongoDBMappingException("you don't have permission to access this field, properly there doesn't a setter exists.", e);
				} catch (InstantiationException e) {
					e.printStackTrace();
					throw new MongoDBMappingException("can not instance complex property.", e);
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					throw new MongoDBMappingException("can not set the field value.", e);
				}
			}
		}
		return po;
	}
}
