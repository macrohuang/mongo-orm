package github.macrohuang.orm.mongo.base.po;

import github.macrohuang.orm.mongo.annotation.Document;
import github.macrohuang.orm.mongo.annotation.MongoField;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Document(db = "test", collection = "test")
public class TestPO implements Serializable {
	/**
     * 
     */
	private static final long serialVersionUID = -975960716165479997L;

	@MongoField(field = "_id")
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

	@Override
	public String toString() {
		return "TestPO [id=" + id + ", accountId=" + accountId + ", groupId=" + groupId + ", groupName=" + groupName + ", descs="
		        + Arrays.toString(descs) + ", descs2=" + descs2 + ", desc3=" + desc3 + "]";
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

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

}
