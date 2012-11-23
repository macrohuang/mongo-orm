package github.macrohuang.odm.mongo.base;

import github.macrohuang.odm.mongo.core.MongoDBTemplate;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class BaseTest {
	protected ApplicationContext applicationContext;
	protected MongoDBTemplate template;

	public BaseTest() {
		applicationContext = new ClassPathXmlApplicationContext("applicationContext-test.xml");
		template = (MongoDBTemplate) applicationContext.getBean("mongoDBTemplate");
	}

	public static void main(String[] args) {
		BaseTest baseTest = new BaseTest();
		System.out.println(baseTest.template);
	}
}
