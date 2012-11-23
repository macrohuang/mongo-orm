package github.macrohuang.odm.mongo.query;

public enum Order {
	ASC(1), DESC(-1);
	int order;

	private Order(int order) {
		this.order = order;
	}

	public int getOrder() {
		return order;
	}
}
