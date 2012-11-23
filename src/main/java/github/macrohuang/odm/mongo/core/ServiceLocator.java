package github.macrohuang.odm.mongo.core;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ServiceLocator {

	private static final ServiceLocator serviceLocator = new ServiceLocator();
	private ApplicationContext applicationContext;
	private ServiceLocator() {
		applicationContext = new ClassPathXmlApplicationContext("applicationContext-mongo-orm.xml");
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public static ServiceLocator getInstance() {
		return serviceLocator;
	}
}
