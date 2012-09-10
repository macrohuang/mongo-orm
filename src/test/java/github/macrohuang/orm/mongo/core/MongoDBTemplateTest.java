package github.macrohuang.orm.mongo.core;

import github.macrohuang.orm.mongo.base.BaseTest;
import github.macrohuang.orm.mongo.base.po.CpcIdea;
import github.macrohuang.orm.mongo.base.po.EmbedPO;
import github.macrohuang.orm.mongo.base.po.TestPO;
import github.macrohuang.orm.mongo.config.DBChooser;
import github.macrohuang.orm.mongo.query.Query;
import github.macrohuang.orm.mongo.query.QueryOperators;
import github.macrohuang.orm.mongo.util.DBObjectUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import junit.framework.Assert;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

public class MongoDBTemplateTest extends BaseTest {
	private static final Logger LOGGER = Logger.getLogger(MongoDBTemplateTest.class);
	@Test
	public void testFindByExample() {
		TestPO examplePo = new TestPO();
		LOGGER.info(template.findByExample(examplePo).toString());
	}

	@Test
	public void testFindByExample2() {
		CpcIdea cpcIdea = new CpcIdea();
		cpcIdea.setAccountId(8764L);
		long time = System.currentTimeMillis();
		LOGGER.info(template.findByExample(DBChooser.getDbAndCollection("idea2012", "idea"), cpcIdea).toString());
		LOGGER.info("cost:" + (System.currentTimeMillis() - time));
	}

	@Test
	public void testSave() {
		TestPO testPO = new TestPO();
		testPO.setAccountId(1234L);
		testPO.setId("1234_" + System.currentTimeMillis());
		testPO.setDescs(new String[] { "abc", "123" });
		testPO.setDesc3(Arrays.asList("12345", "23456", "34567"));
		testPO.setDescs2(new HashSet<String>(testPO.getDesc3()));
		template.insert(testPO);
		System.out.println(testPO);
	}

	@Test
	public void testSaveWithSpecMid() {
		TestPO testPO = new TestPO();
		testPO.setAccountId(1234L);
		testPO.setMid("mongodb_id_" + System.currentTimeMillis());
		testPO.setId("1234_" + System.currentTimeMillis());
		testPO.setDescs(new String[] { "abc", "123" });
		testPO.setDesc3(Arrays.asList("12345", "23456", "34567"));
		testPO.setDescs2(new HashSet<String>(testPO.getDesc3()));
		template.insert(testPO);
		System.out.println(testPO);
	}

	@Test
	public void testSaveOrUpdateWithSpecMid() {
		TestPO testPO = new TestPO();
		testPO.setAccountId(1234L);
		testPO.setMid("mongodb_id_" + System.currentTimeMillis());
		testPO.setId("1111_" + System.currentTimeMillis());
		testPO.setDescs(new String[] { "abc", "123" });
		testPO.setDesc3(Arrays.asList("12345", "23456", "34567"));
		testPO.setDescs2(new HashSet<String>(testPO.getDesc3()));
		template.insert(testPO);
		System.out.println(testPO);
		testPO.setId("2222_" + System.currentTimeMillis());
		template.saveOrUpdate(testPO);
		System.out.println(testPO);
	}

	@Test
	public void testSaveOrUpdate() {
		TestPO testPO = new TestPO();
		testPO.setAccountId(1234L);
		testPO.setId("1234_" + System.currentTimeMillis());
		testPO.setDescs(new String[] { "abc", "123" });
		testPO.setDesc3(Arrays.asList("12345", "23456", "34567"));
		testPO.setDescs2(new HashSet<String>(testPO.getDesc3()));
		template.insert(testPO);
		System.out.println(testPO);
		testPO.setId("2345_" + System.currentTimeMillis());
		template.saveOrUpdate(testPO);
		System.out.println(testPO);
	}

	@Test
	public void testQuery3() {
		Query query = new Query(TestPO.class);
		query.addCondition("date", QueryOperators.GTE, "20120425 16:54:02");
		query.and("date", QueryOperators.LTE, "20120425 16:54:05").include("id").include("accountId").include("date");
		LOGGER.info(template.query(query));
	}

	@Test
	public void testQueryIn() {
		Query query = new Query(TestPO.class);
		query.addCondition("id", QueryOperators.IN, Arrays.asList("1234_1335344039969", "1234_1335344042255", "1234_1335344045674"));
		LOGGER.info(template.query(query));
	}

	@Test
	public void testDelete() {
		TestPO testPO = new TestPO();
		Assert.assertTrue(template.delete(testPO));
	}

	@Test
	public void testQuery() {
		Query query = new Query(TestPO.class).addCondition("id", QueryOperators.EQ, "1234_1335336475050");
		LOGGER.info(template.query(query));
	}

	@Test
	public void testQueryEmbed() {
		Query query = new Query(EmbedPO.class).addCondition("province", QueryOperators.EQ, "Beijing");
		LOGGER.info(template.query(query));
	}

	@Test
	public void testQueryEmbed2() {
		Query query = new Query(EmbedPO.class).addCondition("name", QueryOperators.EQ, "name");
		LOGGER.info(template.query(query));
	}

	@Test
	public void testUpdate() {
		Query query = new Query(TestPO.class).addCondition("id", QueryOperators.EQ, "1234_1335020510874");
		TestPO testPO = new TestPO();
		testPO.setAccountId(2222L);
		testPO.setDesc3(new ArrayList<String>());
		testPO.setDescs(new String[] {});
		testPO.setDescs2(new HashSet<String>());
		testPO.setGroupId(2222L);
		testPO.setGroupName("Group name from update");
		LOGGER.info(template.update(query, testPO));
		LOGGER.info(template.query(query));
	}

	@Test
	public void testUpdateEmbed() {
		Query query = new Query(EmbedPO.class).addCondition("province", QueryOperators.EQ, "Beijing");
		EmbedPO po = new EmbedPO("name", 17, "Beijing", "Beijing", "海淀");
		LOGGER.info(template.update(query, po));
		LOGGER.info(template.query(query));
	}

	@Test
	public void testUpdate2() {
		Query query = new Query(TestPO.class).addCondition("id", QueryOperators.EQ, "1234_1335020510874");
		TestPO testPO = new TestPO();
		testPO.setAccountId(3333L);
		testPO.setDesc3(new ArrayList<String>());
		testPO.setDescs(new String[] {});
		testPO.setDescs2(new HashSet<String>());
		LOGGER.info(template.update(query, testPO, false, false));
		LOGGER.info(template.query(query));
	}

	@Test
	public void testUpdate3() {
		Query query = new Query(TestPO.class).addCondition("mid", QueryOperators.EQ, "8764_201204010000");
		TestPO testPO = new TestPO();
		LOGGER.info(template.update(query, testPO, true, false));
		LOGGER.info(template.query(query));
	}

	@Test
	public void testGetCountByExample() {
		LOGGER.info(template.getCountByExample(DBChooser.getDbAndCollection("idea2012", "idea"), new CpcIdea()));

	}

	public static void main(String[] args) {
		TestPO testPO = new TestPO();
		testPO.setAccountId(235L);
		try {
			// BeanUtils.setProperty(testPO, "consume", new Double(2.5));
			BeanUtils.setProperty(testPO, "consume", new Long(2));
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LOGGER.info(DBObjectUtil.convertPO2DBObject(testPO));
	}
}
