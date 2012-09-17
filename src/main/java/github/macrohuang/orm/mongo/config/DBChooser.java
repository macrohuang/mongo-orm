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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((collection == null) ? 0 : collection.hashCode());
		result = prime * result + ((db == null) ? 0 : db.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DBChooser other = (DBChooser) obj;
		if (collection == null) {
			if (other.collection != null)
				return false;
		} else if (!collection.equals(other.collection))
			return false;
		if (db == null) {
			if (other.db != null)
				return false;
		} else if (!db.equals(other.db))
			return false;
		return true;
	}
}
