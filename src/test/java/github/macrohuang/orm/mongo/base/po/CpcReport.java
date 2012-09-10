package github.macrohuang.orm.mongo.base.po;

import github.macrohuang.orm.mongo.annotation.Document;
import github.macrohuang.orm.mongo.annotation.MongoField;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

@Document(db = "cpcreport", collection = "cpc")
public class CpcReport {
	@MongoField
	public String id;
	@MongoField(field = "accountid")
	public Long accountId;
	@MongoField(field = "groupid")
	public Long groupId;
	public String groupName;
	@MongoField
	public List<String> desc;
	@MongoField
	public String[] desc2;
	@MongoField
	public Map<String, Object> map;
	@MongoField
	public CpcReport child;

	public CpcReport() {
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

	public List<String> getDesc() {
		return desc;
	}

	public void setDesc(List<String> desc) {
		this.desc = desc;
	}

	public String[] getDesc2() {
		return desc2;
	}

	public void setDesc2(String[] desc2) {
		this.desc2 = desc2;
	}

	public Map<String, Object> getMap() {
		return map;
	}

	public void setMap(Map<String, Object> map) {
		this.map = map;
	}

	public CpcReport getChild() {
		return child;
	}

	public void setChild(CpcReport child) {
		this.child = child;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}