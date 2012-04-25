package github.macrohuang.orm.mongo.core;

import github.macrohuang.orm.mongo.base.BaseTest;
import github.macrohuang.orm.mongo.base.po.CpcIdea;
import github.macrohuang.orm.mongo.base.po.TestPO;
import github.macrohuang.orm.mongo.config.DBChooser;
import github.macrohuang.orm.mongo.query.Query;
import github.macrohuang.orm.mongo.query.QueryOperators;
import github.macrohuang.orm.mongo.util.DBObjectUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Test;

public class MongoDBTemplateTest extends BaseTest {
	private static final Logger LOGGER = Logger.getLogger(MongoDBTemplateTest.class);
	@Test
	public void testFindByExample() {
		TestPO examplePo = new TestPO();
//		examplePo.setId("1234_1335020510874");
		LOGGER.info(template.findByExample(examplePo).toString());
	}

	@Test
	public void testFindByExample2() {
		CpcIdea cpcIdea = new CpcIdea();
		cpcIdea.setAccountId(8764L);
		cpcIdea.setId(328695107L);
		// examplePo.setId("1234_1335020510874");
		long time = System.currentTimeMillis();
		LOGGER.info(template.findByExample(DBChooser.getDbAndCollection("idea2012", "idea"), cpcIdea).toString());
		LOGGER.info("cost:" + (System.currentTimeMillis() - time));
	}

	@Test
	public void testSave() {
		TestPO testPO = new TestPO();
		testPO.setAccountId(1234L);
		testPO.setGroupId(1356L);
		testPO.setId("1234_" + System.currentTimeMillis());
		testPO.setGroupName("中文");
		testPO.setDescs(new String[] { "abc", "123" });
		testPO.setDesc3(Arrays.asList("12345", "23456", "34567"));
		testPO.setDescs2(new HashSet<String>(testPO.getDesc3()));
		template.save(testPO);
		Assert.assertEquals(testPO.getId(), template.findByExample(testPO).get(0).getId());
		// Assert.assertTrue(template.delete(testPO));
	}

	@Test
	public void testQuery() {
		Query query = new Query(TestPO.class).addCondition("id", QueryOperators.EQ, "1234_1335336475050");
		LOGGER.info(template.query(query));
	}

	@Test
	public void testQuery2() {
		Query query = new Query(CpcIdea.class).addCondition("accountId", QueryOperators.EQ, 8764L).addCondition("id", QueryOperators.EQ, 328695107L);
		LOGGER.info(template.query(DBChooser.getDbAndCollection("idea2012", "idea"), query));
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
	public void testUpdate2() {
		Query query = new Query(TestPO.class).addCondition("id", QueryOperators.EQ, "1234_1335020510874");
		TestPO testPO = new TestPO();
		testPO.setAccountId(3333L);
		testPO.setDesc3(new ArrayList<String>());
		testPO.setDescs(new String[] {});
		testPO.setDescs2(new HashSet<String>());
		testPO.setGroupId(3333L);
		testPO.setGroupName("Group name from update, not multi");
		LOGGER.info(template.update(query, testPO, false, false));
		LOGGER.info(template.query(query));
	}

	@Test
	public void testUpdate3() {
		Query query = new Query(TestPO.class).addCondition("accountId", QueryOperators.EQ, 1234L);
		TestPO testPO = new TestPO();
		testPO.setGroupId(4444L);
		testPO.setGroupName("4444");
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
		testPO.setGroupName("Name");
		LOGGER.info(DBObjectUtil.convertPO2DBObject(testPO));
	}
}
