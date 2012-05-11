package github.macrohuang.orm.mongo.util;

import github.macrohuang.orm.mongo.annotation.Embed;
import github.macrohuang.orm.mongo.annotation.MongoField;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class FieldCacheMap {
	private Map<Class<?>, Map<String, Field>> cacheMap;
	private Map<Class<?>, Set<String>> embed;

	public FieldCacheMap() {
		cacheMap = new ConcurrentHashMap<Class<?>, Map<String, Field>>();
		embed = new ConcurrentHashMap<Class<?>, Set<String>>();
	}

	public <T> void addPo(T po) {
		if (!cacheMap.containsKey(po.getClass())) {
			Map<String, Field> fields = new HashMap<String, Field>();
			cacheMap.put(po.getClass(), fields);
			for (Field field : po.getClass().getDeclaredFields()) {
				if (field.getAnnotation(MongoField.class) != null) {
					fields.put(DBObjectUtil.getMongoField(field), field);
				}
				if (field.getAnnotation(Embed.class) != null) {
					if (!embed.containsKey(po.getClass())) {
						embed.put(po.getClass(), new HashSet<String>());
					}
					Embed e = field.getAnnotation(Embed.class);
					embed.get(po.getClass()).add(e.parent());
				}
			}
		}
	}

	public <T> boolean isEmbed(T po, String mongoKey) {
		if (!embed.containsKey(po.getClass()))
			return false;
		return (embed.get(po.getClass()).contains(mongoKey));
	}

	public <T> boolean isPoField(T po, String mongoKey) {
		if (!cacheMap.containsKey(po.getClass()))
			return false;
		return cacheMap.get(po.getClass()).containsKey(mongoKey);
	}

	public <T> Field getPoField(T po, String mongoKey) {
		if (!cacheMap.containsKey(po.getClass()))
			return null;
		return cacheMap.get(po.getClass()).get(mongoKey);
	}

	public <T> Set<String> getAllMongoField(T po) {
		if (!cacheMap.containsKey(po.getClass())) {
			throw new UnsupportedOperationException("This object does not mapped by Mongo-orm");
		}
		return cacheMap.get(po.getClass()).keySet();
	}
}
