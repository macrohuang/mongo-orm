package github.macrohuang.orm.mongo.config;


public final class DBChooser {
	private String db;
	private String collection;

	public static DBChooser getDbAndCollection(String db, String collection) {
		return new DBChooser(db, collection);
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
}
