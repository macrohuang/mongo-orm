package github.macrohuang.orm.mongo.base.po;

import github.macrohuang.orm.mongo.annotation.Document;
import github.macrohuang.orm.mongo.annotation.MongoField;

@Document(db = "test")
public class SimplePO {
	@MongoField
	private Long field1;
	@MongoField
	private Integer field2;
	@MongoField
	private String field3;
	@MongoField
	private Double field4;

	public SimplePO() {
	}
	public SimplePO(Long field1, Integer field2, String field3, Double field4) {
		super();
		this.field1 = field1;
		this.field2 = field2;
		this.field3 = field3;
		this.field4 = field4;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj instanceof SimplePO) {
			SimplePO simplePO = (SimplePO) obj;
			return ((field1 == simplePO.getField1() || field1 != null && simplePO.getField1() != null && field1.equals(simplePO.getField1()))
					&& (field2 == simplePO.getField2() || field2 != null && simplePO.getField2() != null && field2.equals(simplePO.getField2()))
					&& (field3 == simplePO.getField3() || field3 != null && simplePO.getField3() != null && field3.equals(simplePO.getField3())) && (field4 == simplePO
					.getField4() || field4 != null && simplePO.getField4() != null && field4.equals(simplePO.getField4())));
		} else {
			return false;
		}
	}
	public Long getField1() {
		return field1;
	}

	public void setField1(Long field1) {
		this.field1 = field1;
	}

	public Integer getField2() {
		return field2;
	}

	public void setField2(Integer field2) {
		this.field2 = field2;
	}

	public String getField3() {
		return field3;
	}

	public void setField3(String field3) {
		this.field3 = field3;
	}

	public Double getField4() {
		return field4;
	}

	public void setField4(Double field4) {
		this.field4 = field4;
	}
}
