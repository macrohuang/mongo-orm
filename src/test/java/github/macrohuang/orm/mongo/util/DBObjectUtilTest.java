package github.macrohuang.orm.mongo.util;

import github.macrohuang.orm.mongo.base.po.CpcReport;
import github.macrohuang.orm.mongo.base.po.EmbedPO;
import github.macrohuang.orm.mongo.base.po.SimplePO;
import junit.framework.Assert;

import org.junit.Test;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class DBObjectUtilTest {
	private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(DBObjectUtilTest.class.getName());

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
		Assert.assertNotNull(dbObject);
	}

	@Test
	public void testFillDocument2PO() {
		DBObject dbObject = new BasicDBObject();
		dbObject.put("field1", 1L);
		dbObject.put("field2", 2);
		dbObject.put("field3", "3");
		dbObject.put("field4", 4.0f);
		SimplePO simplePO = new SimplePO();
		DBObjectUtil.fillDocument2PO(dbObject, simplePO);
		Assert.assertNotNull(simplePO);
		SimplePO except = new SimplePO(new Long(1L), new Integer(2), "3", new Double(4.0f));
		Assert.assertEquals(except, simplePO);
	}

	@Test
	public void testFillDocument2POEmbed() {
		DBObject dbObject = new BasicDBObject();
		dbObject.put("name", "name");
		dbObject.put("age", 17);
		dbObject.put("address", new BasicDBObject() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -8242795054766566073L;

			{
				put("province", "Beijing");
				put("city", "Beijing");
				put("region", "Haidian");
			}
		});
		EmbedPO po = new EmbedPO();
		DBObjectUtil.fillDocument2PO(dbObject, po);
		Assert.assertNotNull(po);
		EmbedPO except = new EmbedPO("name", 17, "Beijing", "Beijing", "Haidian");
		Assert.assertEquals(except, po);
	}

	@Test
	public void testConvertPO2DBObjectEmbed() {
		EmbedPO po = new EmbedPO("name", 17, "Beijing", "Beijing", "Haidian");
		DBObject dbObject = DBObjectUtil.convertPO2DBObject(po);
		LOGGER.info(dbObject.toString());
		Assert.assertNotNull(dbObject);
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
