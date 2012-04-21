package github.macrohuang.orm.mongo.query;

public enum QueryOperators {
	GT("$gt"), GTE("$gte"), LT("$lt"), LTE("$lte"), NE("$ne"), EQ(""), IN("$in"), NIN("$nin"),
	// The following six type are not support yet.
	// MOD("$mod"),
	// ALL("$all"),
	// SIZE("$size"),
	// EXISTS("$exists"),
	// WHERE("$where"),
	// NEAR("$near")
	;

	private String operate;

	private QueryOperators(String operate) {
		this.operate = operate;
	}

	public String getOperate() {
		return operate;
	}

	public void setOperate(String operate) {
		this.operate = operate;
	}
}
