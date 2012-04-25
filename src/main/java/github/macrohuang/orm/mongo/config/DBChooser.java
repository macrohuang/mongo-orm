package github.macrohuang.orm.mongo.config;

import github.macrohuang.orm.mongo.core.Assert;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class DBChooser {
	private String db;
	private String collection;
	private static final Map<String, DBChooser> dbchoosers = new ConcurrentHashMap<String, DBChooser>();

	public static DBChooser getDbAndCollection(String db, String collection) {
		Assert.assertNotBlank(db);
		Assert.assertNotBlank(collection);
		if (!dbchoosers.containsKey(db.trim() + "_" + collection.trim())) {
			dbchoosers.put(db.trim() + "_" + collection.trim(), new DBChooser(db, collection));
		}
		return dbchoosers.get(db.trim() + "_" + collection.trim());
	}

	private DBChooser(String db, String collection) {
		this.db = db;
		this.collection = collection;
	}

	public String getDb() {
		return db;
	}

	public String getCollection() {
		return collection;
	}

	@Override
	public String toString() {
		return "DBChooser [db=" + db + ", collection=" + collection + "]";
	}
}
