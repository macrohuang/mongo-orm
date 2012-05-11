package github.macrohuang.orm.mongo.util;

import github.macrohuang.orm.mongo.annotation.Embed;
import github.macrohuang.orm.mongo.annotation.MongoField;
import github.macrohuang.orm.mongo.exception.MongoDBMappingException;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class FieldCacheMap {
	private Map<Class<?>, Map<String, Field>> docKeyFieldMap;
	private Map<Class<?>, Map<String, String>> fieldDocKeyMap;
	private Map<Class<?>, Set<String>> embed;
	private static final FieldCacheMap INSTANCE = new FieldCacheMap();

	public static FieldCacheMap getInstance() {
		return INSTANCE;
	}

	private FieldCacheMap() {
		docKeyFieldMap = new ConcurrentHashMap<Class<?>, Map<String, Field>>();
		embed = new ConcurrentHashMap<Class<?>, Set<String>>();
		fieldDocKeyMap = new ConcurrentHashMap<Class<?>, Map<String, String>>();
	}

	public void addIfAbsent(Class<?> class1) {
		if (!docKeyFieldMap.containsKey(class1)) {
			Map<String, String> field2doc = new HashMap<String, String>();
			Map<String, Field> doc2field = new HashMap<String, Field>();
			for (Field field : class1.getDeclaredFields()) {
				if (field.getAnnotation(MongoField.class) != null) {
					doc2field.put(DBObjectUtil.getMongoField(field), field);
					field2doc.put(field.getName(), DBObjectUtil.getMongoField(field));
				}
				if (field.getAnnotation(Embed.class) != null) {
					if (!embed.containsKey(class1)) {
						embed.put(class1, new HashSet<String>());
					}
					Embed e = field.getAnnotation(Embed.class);
					embed.get(class1).add(e.parent());
				}
			}
			fieldDocKeyMap.put(class1, field2doc);
			docKeyFieldMap.put(class1, doc2field);
		}
	}

	public <T> boolean isFieldMapped(Class<T> class1, String field) {
		if (!fieldDocKeyMap.containsKey(class1))
			return false;
		return fieldDocKeyMap.get(class1).containsKey(field);
	}

	public <T> String getFieldDocKey(Class<T> class1, String field) {
		if (!fieldDocKeyMap.containsKey(class1))
			throw new MongoDBMappingException("Unmapped field:" + field);
		return fieldDocKeyMap.get(class1).get(field);
	}

	public <T> boolean isEmbed(T po, String docKey) {
		if (!embed.containsKey(po.getClass()))
			return false;
		return (embed.get(po.getClass()).contains(docKey));
	}
	
	public <T> boolean isEmbed(Class<T> class1, String field) {
		try {
			return class1.getDeclaredField(field) != null && class1.getDeclaredField(field).getAnnotation(Embed.class) != null;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public <T> String getEmbedParent(Class<T> class1, String field) {
		try {
			return class1.getDeclaredField(field).getAnnotation(Embed.class).parent();
		} catch (Exception e) {
			e.printStackTrace();
			throw new MongoDBMappingException("Not as embed field:" + field);
		}
	}

	public <T> boolean isPoField(Class<T> class1, String mongoKey) {
		if (!docKeyFieldMap.containsKey(class1))
			return false;
		return docKeyFieldMap.get(class1).containsKey(mongoKey);
	}

	public <T> Field getPoField(Class<T> class1, String mongoKey) {
		if (!docKeyFieldMap.containsKey(class1)) {
			throw new MongoDBMappingException("Unmapped field:" + mongoKey);
		}
		return docKeyFieldMap.get(class1).get(mongoKey);
	}

	public <T> Set<String> getAllMongoField(Class<T> class1) {
		if (!docKeyFieldMap.containsKey(class1)) {
			throw new MongoDBMappingException("Unmapped po:" + class1);
		}
		return docKeyFieldMap.get(class1).keySet();
	}
}
