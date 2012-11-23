package github.macrohuang.odm.mongo.base.po;

import github.macrohuang.odm.mongo.annotation.Document;
import github.macrohuang.odm.mongo.annotation.MongoField;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

@Document(db = "test", collection = "test")
public class TestPO implements Serializable {
	/**
     * 
     */
	private static final long serialVersionUID = -975960716165479997L;

	// @MongoId
	@MongoField(field = "_id")
	private String mid;

	@MongoField
	private String id;

	@MongoField(field = "accountid")
	private Long accountId;

	@MongoField(field = "groupid")
	private Long groupId;

	@MongoField(field = "groupname")
	private String groupName;

	@MongoField
	private String[] descs;

	@MongoField
	private Set<String> descs2;

	@MongoField
	private List<String> desc3;

	public List<String> getDesc3() {
		return desc3;
	}


	public void setDesc3(List<String> desc3) {
		this.desc3 = desc3;
	}

	public Set<String> getDescs2() {
		return descs2;
	}

	public void setDescs2(Set<String> descs2) {
		this.descs2 = descs2;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String[] getDescs() {
		return descs;
	}

	public void setDescs(String[] descs) {
		this.descs = descs;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
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
