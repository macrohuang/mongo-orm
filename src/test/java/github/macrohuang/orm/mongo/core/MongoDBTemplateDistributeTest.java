package github.macrohuang.orm.mongo.core;

import github.macrohuang.orm.mongo.base.DistributeBaseTest;
import github.macrohuang.orm.mongo.base.po.CpcIdea;
import github.macrohuang.orm.mongo.base.po.TestPO;
import github.macrohuang.orm.mongo.config.DBChooser;

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
		cpcIdea.setId(328695107L);
		long time = System.currentTimeMillis();
		LOGGER.info(template.findByExample(DBChooser.getDbAndCollection("CPCREPORT_2012", "CPCIDEA"), cpcIdea).toString());
		LOGGER.info("cost:" + (System.currentTimeMillis() - time));
	}
}
