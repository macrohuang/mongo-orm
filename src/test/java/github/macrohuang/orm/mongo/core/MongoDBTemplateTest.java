package github.macrohuang.orm.mongo.core;

import github.macrohuang.orm.mongo.base.BaseTest;
import github.macrohuang.orm.mongo.base.TestPO;

import java.util.Arrays;
import java.util.HashSet;

import junit.framework.Assert;

import org.junit.Test;

public class MongoDBTemplateTest extends BaseTest {
	@Test
	public void testFindByExample() {
		MongoDBTemplate template = (MongoDBTemplate) applicationContext.getBean("mongoDBTemplate");
		System.out.println(template.findByExample(new TestPO()).toString());
	}

	@Test
	public void testSave() {
		MongoDBTemplate template = (MongoDBTemplate) applicationContext.getBean("mongoDBTemplate");
		TestPO testPO = new TestPO();
		testPO.setAccountId(1234L);
		testPO.setGroupId(1356L);
		testPO.setId("1234_" + System.currentTimeMillis());
		testPO.setGroupName("ÖÐÎÄÃû");
		testPO.setDescs(new String[] { "abc", "123" });
		testPO.setDesc3(Arrays.asList("12345", "23456", "34567"));
		testPO.setDescs2(new HashSet<String>(testPO.getDesc3()));
		template.save(testPO);
		Assert.assertEquals(testPO.getId(), template.findByExample(testPO).get(0).getId());
		Assert.assertTrue(template.delete(testPO));
	}
}
