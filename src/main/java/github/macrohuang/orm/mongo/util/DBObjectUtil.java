package github.macrohuang.orm.mongo.util;


import github.macrohuang.orm.mongo.annotation.Document;
import github.macrohuang.orm.mongo.annotation.Embed;
import github.macrohuang.orm.mongo.annotation.MongoField;
import github.macrohuang.orm.mongo.constant.Constants;
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

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.util.StringUtils;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class DBObjectUtil {
	private static final FieldCacheMap FIELD_CACHE_MAP = FieldCacheMap.getInstance();
	private static final Logger logger = Logger.getLogger(DBObjectUtil.class);

	public static DBObject convertPO2DBObject(Object object) throws MongoDBMappingException {
		if (Constants.convertLogEnable)
			logger.info("convert " + object + " into DBObject");
		return convertPO2DBObject(object, 1);
	}

	public static DBObject convertPO2DBObject(Object object, int deepth) throws MongoDBMappingException {
		if (Constants.convertLogEnable)
			logger.info("convert " + object + " into DBObject with deepth: " + deepth);
		return convertPO2DBObjectInner(object, false, deepth, 0);
	}

	public static DBObject convertPO2DBObject(Object object, boolean nullable, int deepth) throws MongoDBMappingException {
		if (Constants.convertLogEnable)
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
		FIELD_CACHE_MAP.addIfAbsent(object.getClass());
		DBObject dbObject;
		if (object instanceof Collection) {
			dbObject = new BasicDBList();
		} else {
			dbObject = new BasicDBObject();
		}
		Field field;
		for (String docKey : FIELD_CACHE_MAP.getAllMongoField(object.getClass())) {
			field = FIELD_CACHE_MAP.getPoField(object.getClass(), docKey);
			field.setAccessible(true);
			try {
				if (field.getType().getAnnotation(Document.class) != null) {
					dbObject.put(docKey, convertPO2DBObjectInner(field.get(object), nullable, deepth, currentDepth + 1));
				} else if (field.getAnnotation(Embed.class) != null) {// Embed
																		// document.
					Embed embed = field.getAnnotation(Embed.class);
					if (!dbObject.containsField(embed.parent())) {
						dbObject.put(embed.parent(), new BasicDBObject());
					}
					if (nullable || !nullable && field.get(object) != null) {
						((DBObject) dbObject.get(embed.parent())).put(docKey, field.get(object));
					}
				} else {
					if (nullable || !nullable && field.get(object) != null) {
						if (FIELD_CACHE_MAP.isId(object, docKey)) {
							if (ObjectId.isValid(String.valueOf(field.get(object)))) {
								dbObject.put(docKey, new ObjectId(String.valueOf(field.get(object))));
							} else {
								dbObject.put(docKey, String.valueOf(field.get(object)));
							}
						} else {
							dbObject.put(docKey, field.get(object));
						}
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
		if (Constants.convertLogEnable)
			logger.info("Convert resutl:" + dbObject);
		return dbObject;
	}

	public static Method getFieldSetterMethod(Object object, Field field) throws SecurityException, NoSuchMethodException {
		StringBuilder getterMethod = new StringBuilder();
		getterMethod.append("set").append(("" + field.getName().charAt(0)).toUpperCase()).append(field.getName().substring(1));
		return object.getClass().getMethod(getterMethod.toString(), field.getType());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T fillDocument2PO(DBObject dbObject, T po) {
		if (po == null)
			throw new MongoDBMappingException("can't not fill a document into a null po.");
		FIELD_CACHE_MAP.addIfAbsent(po.getClass());
		Field field;
		for (String mongoField : dbObject.keySet()) {
			if (FIELD_CACHE_MAP.isPoField(po.getClass(), mongoField)) {
				field = FIELD_CACHE_MAP.getPoField(po.getClass(), mongoField);
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
									} else {
										field.set(po, docVal);
									}
								} else {
									field.set(po, docVal);
								}
							}
						}
					} else if (FIELD_CACHE_MAP.isId(po, mongoField)) {
						field.set(po, String.valueOf(docVal));
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
					e.printStackTrace();
					throw new MongoDBMappingException("can not set the field value.", e);
				}
			} else if (FIELD_CACHE_MAP.isEmbed(po, mongoField) && dbObject.get(mongoField) instanceof DBObject) {
				return fillDocument2PO((DBObject) dbObject.get(mongoField), po);
			}
		}
		return po;
	}

	public static <T> void setEntryId(DBObject object, T po) {
		Field field = FIELD_CACHE_MAP.getPoField(po.getClass(), Constants.MONGO_ID);
		if (field != null) {
			try {
				field.set(po, object.get(Constants.MONGO_ID).toString());
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				throw new MongoDBMappingException("setter argument does match", e);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				throw new MongoDBMappingException("you don't have permission to access this field, properly there doesn't a setter exists.", e);
			}
		}
	}

	public static <T> Map<String, Object> convertDBObjectToPoMap(DBObject dbObject, T po) {
		if (po == null)
			throw new MongoDBMappingException("can't not fill a document into a null po.");
		Map<String, Object> result = new HashMap<String, Object>();
		FIELD_CACHE_MAP.addIfAbsent(po.getClass());
		Field field;
		for (String mongoField : dbObject.keySet()) {
			if (FIELD_CACHE_MAP.isPoField(po.getClass(), mongoField)) {
				field = FIELD_CACHE_MAP.getPoField(po.getClass(), mongoField);
				result.put(field.getName(), dbObject.get(mongoField));
			}
		}
		return result;
	}
}
