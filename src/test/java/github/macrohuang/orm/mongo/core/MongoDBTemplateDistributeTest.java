package github.macrohuang.orm.mongo.core;

import github.macrohuang.orm.mongo.base.DistributeBaseTest;
import github.macrohuang.orm.mongo.base.po.CpcIdea;
import github.macrohuang.orm.mongo.base.po.TestPO;
import github.macrohuang.orm.mongo.config.DBChooser;
import github.macrohuang.orm.mongo.query.Order;
import github.macrohuang.orm.mongo.query.Query;
import github.macrohuang.orm.mongo.query.QueryOperators;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.junit.Test;

public class MongoDBTemplateDistributeTest extends DistributeBaseTest {
	private static final Logger LOGGER = Logger.getLogger(MongoDBTemplateDistributeTest.class);

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
		LOGGER.info(template.findByExample(DBChooser.getDbAndCollection("idea2012", "idea"), cpcIdea).size());
		LOGGER.info("cost:" + (System.currentTimeMillis() - time));
	}

	@Test
	public void testQuery() {
		Query query = new Query(CpcIdea.class);
		query.addCondition("mid", QueryOperators.GTE, "8764_20120401");
		long time = System.currentTimeMillis();
		LOGGER.info(template.query(DBChooser.getDbAndCollection("idea2012", "idea"), query).getTotalCount());
		LOGGER.info("cost:" + (System.currentTimeMillis() - time));
	}

	@Test
	public void testQuerySort() {
		Query query = new Query(CpcIdea.class);
		query.addCondition("mid", QueryOperators.IN, new String[] { "31862_201204010000", "8764_201204010000", "8651_201204010000",
				"21666_201204010000", "28951_201204010000" });
		query.addOrder("accountId", Order.DESC).addOrder("ideaId", Order.DESC);

		long time = System.currentTimeMillis();
		LOGGER.info(template.query(DBChooser.getDbAndCollection("idea2012", "idea"), query).getTotalCount());
		LOGGER.info("cost:" + (System.currentTimeMillis() - time));
	}

	@Test
	public void testSaveReturnId() {
		TestPO testPO = new TestPO();
		testPO.setMid("1234343423");
		testPO.setAccountId(12345L);
		System.out.println(template.insert(testPO));
	}

	@Test
	public void testSaveOrUpdate() {
		TestPO testPO = new TestPO();
		testPO.setMid("1234343424");
		testPO.setAccountId(12345L);
		System.out.println(template.insert(testPO));
		System.out.println(testPO);

		testPO.setAccountId(23456L);
		System.out.println(template.saveOrUpdate(testPO));
		System.out.println(testPO);
	}

	class QueryJob implements Runnable {
		Query query;
		CountDownLatch countDownLatch;

		public QueryJob(Query query, CountDownLatch countDownLatch) {
			this.query = query;
			this.countDownLatch = countDownLatch;
		}

		@Override
		public void run() {
			countDownLatch.countDown();
			List<Map<String, Object>> result = template.queryForRaw(query);
			StringBuilder sb = new StringBuilder();
			for (Map<String, Object> data : result) {
				for (String key : data.keySet()) {
					sb.append(data.get(key));
				}
			}
			LOGGER.info("total size:" + result.size());
			LOGGER.info(sb.toString().length() > 100000 ? "true" : "false");
		}
	}
}
