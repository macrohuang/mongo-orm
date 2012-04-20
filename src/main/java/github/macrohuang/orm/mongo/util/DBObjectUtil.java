package github.macrohuang.orm.mongo.util;

import github.macrohuang.orm.mongo.annotation.Document;
import github.macrohuang.orm.mongo.annotation.MongoField;
import github.macrohuang.orm.mongo.exception.MongoDBMappingException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.util.StringUtils;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class DBObjectUtil {
	private static final Map<String, Field[]> FIELD_CACHE_MAP = new ConcurrentHashMap<String, Field[]>();

	public static DBObject convertPO2DBObject(Object object) throws MongoDBMappingException {
		return convertPO2DBObject(object, 1);
	}

	public static DBObject convertPO2DBObject(Object object, int deepth) throws MongoDBMappingException {
		return convertPO2DBObjectInner(object, deepth, 0);
	}

	public static Method getFieldGetterMethod(Object object, Field field)
			throws SecurityException, NoSuchMethodException {
		StringBuilder getterMethod = new StringBuilder();
		getterMethod.append("get").append(("" + field.getName().charAt(0)).toUpperCase()).append(field.getName().substring(1));
		return object.getClass().getMethod(getterMethod.toString(), new Class[0]);
	}

	private static String getMongoField(Field field) {
		MongoField mongoField = field.getAnnotation(MongoField.class);
		return StringUtils.hasText(mongoField.field()) ? mongoField.field() : field.getName();
	}

	private static DBObject convertPO2DBObjectInner(Object object, int deepth,
			int currentDepth) throws MongoDBMappingException {
		if (object == null || currentDepth == deepth) {
			return new BasicDBObject();
		}
		Field[] fields = FIELD_CACHE_MAP.get(object.getClass().getName());
		if (fields == null) {
			fields = object.getClass().getDeclaredFields();
			FIELD_CACHE_MAP.put(object.getClass().getName(), fields);
		}
		DBObject dbObject;
		if (object instanceof Collection) {
			dbObject = new BasicDBList();
		} else {
			dbObject = new BasicDBObject();
		}
		for (Field field : fields) {
			if (field.getAnnotation(MongoField.class) != null) {
				field.setAccessible(true);
				try {
					if (field.getType().getAnnotation(Document.class) != null) {
						dbObject.put(getMongoField(field),
								convertPO2DBObjectInner(field.get(object), deepth, currentDepth + 1));
//						dbObject.put(getMongoField(field),
//								convertPO2DBObjectInner(getFieldGetterMethod(object, field).invoke(object, new Object[0]), deepth, currentDepth + 1));
					} else {
//						dbObject.put(getMongoField(field), getFieldGetterMethod(object, field).invoke(object, new Object[0]));
//						field.setAccessible(true);
						dbObject.put(getMongoField(field), field.get(object));
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
		}
		return dbObject;
	}

	public static Method getFieldSetterMethod(Object object, Field field) throws SecurityException, NoSuchMethodException {
		StringBuilder getterMethod = new StringBuilder();
		getterMethod.append("set").append(("" + field.getName().charAt(0)).toUpperCase()).append(field.getName().substring(1));
		return object.getClass().getMethod(getterMethod.toString(), field.getType());
	}

	@SuppressWarnings("unchecked")
	public static <T> T fillDocument2PO(DBObject dbObject, T po) {
		if (po == null)
			throw new MongoDBMappingException("can't not fill a document into a null po.");
		if (po.getClass().getAnnotation(Document.class) == null) {
			throw new MongoDBMappingException("can't not fill a document into a non document po.");
		}
		Field[] fields = FIELD_CACHE_MAP.get(po.getClass().getName());
		if (fields == null) {
			fields = po.getClass().getDeclaredFields();
			FIELD_CACHE_MAP.put(po.getClass().getName(), fields);
		}
		for (Field field : fields) {
			if (field.getAnnotation(MongoField.class) != null) {
				try {
					field.setAccessible(true);
					String docKey = getMongoField(field);
					Object docVal = dbObject.get(docKey);
					if (docVal instanceof DBObject) {
						if (field.getType().getAnnotation(Document.class) != null) {
//							getFieldSetterMethod(po, field).invoke(po, fillDocument2PO((DBObject) docVal, (T) field.getType().newInstance()));
							field.set(po, fillDocument2PO((DBObject) docVal, (T) field.getType().newInstance()));
						} else {
//							getFieldSetterMethod(po, field).invoke(po, docVal);
							field.set(po, docVal);
						}
					} else {
//						getFieldSetterMethod(po, field).invoke(po, docVal);
						field.set(po, docVal);
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
				}
			}
		}
		return po;
	}
}
