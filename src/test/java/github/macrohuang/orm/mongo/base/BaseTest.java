package github.macrohuang.orm.mongo.base;

import github.macrohuang.orm.mongo.core.MongoDBTemplate;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class BaseTest {
	protected ApplicationContext applicationContext;
	protected MongoDBTemplate template;

	public BaseTest() {
		applicationContext = new ClassPathXmlApplicationContext("applicationContext-test.xml");
		template = (MongoDBTemplate) applicationContext.getBean("mongoDBTemplate");
	}
}
