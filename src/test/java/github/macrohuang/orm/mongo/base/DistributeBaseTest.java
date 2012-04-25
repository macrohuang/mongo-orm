package github.macrohuang.orm.mongo.base;

import github.macrohuang.orm.mongo.core.MongoDBTemplate;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DistributeBaseTest {
	protected ApplicationContext applicationContext;
	protected MongoDBTemplate template;

	public DistributeBaseTest() {
		applicationContext = new ClassPathXmlApplicationContext("applicationContext-test-distribute.xml");
		template = (MongoDBTemplate) applicationContext.getBean("mongoDBTemplate");
	}
}
