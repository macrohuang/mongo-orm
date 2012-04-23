package github.macrohuang.orm.mongo.base;

import github.macrohuang.orm.mongo.annotation.Document;
import github.macrohuang.orm.mongo.annotation.MongoField;
import github.macrohuang.orm.mongo.util.DBObjectUtil;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import com.mongodb.DBObject;

public class DBObjectUtilTest {
	private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(DBObjectUtilTest.class.getName());
	@Document(db = "cpcreport", collection = "cpc")
	public static class CpcReport {
		@MongoField
		private String id;
		@MongoField(field = "accountid")
		private Long accountId;
		@MongoField(field = "groupid")
		private Long groupId;
		private String groupName;

		@MongoField
		private List<String> desc;

		@MongoField
		private String[] desc2;

		@MongoField
		private Map<String, Object> map;
		@MongoField
		private CpcReport child;

		public CpcReport() {
			this.id = "1_20120412121305";
			this.accountId = 2L;
			this.groupId = 3L;
			this.groupName = "test";
			this.desc = Arrays.asList("a", "b", "c");
			this.desc2 = new String[] { "1", "2", "3" };
			this.map = new HashMap<String, Object>();
			map.put("abc", 1234);
			map.put("bcd", new Date());
		}

		@Override
		public String toString() {
			return "CpcReport [id=" + id + ", accountId=" + accountId + ", groupId=" + groupId + ", groupName=" + groupName + ", desc=" + desc
					+ ", desc2=" + Arrays.toString(desc2) + ", map=" + map + ", child=" + child + "]";
		}

		public String[] getDesc2() {
			return desc2;
		}

		public void setDesc2(String[] desc2) {
			this.desc2 = desc2;
		}

		public List<String> getDesc() {
			return desc;
		}

		public void setDesc(List<String> desc) {
			this.desc = desc;
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

		public CpcReport getChild() {
			return child;
		}

		public void setChild(CpcReport child) {
			this.child = child;
		}

	}

	@Test
	public void testConvertPO2DBObjectNoChild() {
		CpcReport cpcReport = new CpcReport();
		DBObject dbObject = DBObjectUtil.convertPO2DBObject(cpcReport);
		LOGGER.info(dbObject.toString());
		Assert.assertNotNull(dbObject);
	}

	@Test
	public void testConvertPO2DBObjectException() {
		Object cpcReport = new Object();
		DBObject dbObject = DBObjectUtil.convertPO2DBObject(cpcReport);
		LOGGER.info(dbObject.toString());
		// Assert.assertNotNull(dbObject);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CpcReport cpcReport = new CpcReport();
		CpcReport child = new CpcReport();
		child.setChild(new CpcReport());
		cpcReport.setChild(child);
		Long l = System.currentTimeMillis();
		DBObject dbObject = DBObjectUtil.convertPO2DBObject(cpcReport, 3);
		System.out.println("DBObject:" + dbObject);
		System.out.println("cost:" + (System.currentTimeMillis() - l));
		l = System.currentTimeMillis();
		System.out.println("CpcReport:" + DBObjectUtil.fillDocument2PO(dbObject, new CpcReport()));
		System.out.println("cost:" + (System.currentTimeMillis() - l));

	}
}
