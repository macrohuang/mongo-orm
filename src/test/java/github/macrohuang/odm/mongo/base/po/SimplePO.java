package github.macrohuang.odm.mongo.base.po;

import github.macrohuang.odm.mongo.annotation.Document;
import github.macrohuang.odm.mongo.annotation.MongoField;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

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
		return EqualsBuilder.reflectionEquals(this, obj);
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
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
