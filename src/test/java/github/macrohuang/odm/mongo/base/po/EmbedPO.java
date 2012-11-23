package github.macrohuang.odm.mongo.base.po;

import github.macrohuang.odm.mongo.annotation.Document;
import github.macrohuang.odm.mongo.annotation.Embed;
import github.macrohuang.odm.mongo.annotation.MongoField;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

@Document(db = "test", collection = "test")
public class EmbedPO {
	@MongoField
	private String name;
	@MongoField
	private Integer age;
	@MongoField
	@Embed(parent = "address")
	private String province;
	@MongoField
	@Embed(parent = "address")
	private String city;
	@MongoField
	@Embed(parent = "address")
	private String region;

	public EmbedPO() {

	}

	public EmbedPO(String name, int age, String province, String city, String region) {
		super();
		this.name = name;
		this.age = age;
		this.province = province;
		this.city = city;
		this.region = region;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);

	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
