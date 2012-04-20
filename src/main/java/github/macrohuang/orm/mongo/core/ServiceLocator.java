package github.macrohuang.orm.mongo.core;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ServiceLocator {

	private static final ServiceLocator serviceLocator = new ServiceLocator();
	private static final Logger LOGGER = Logger.getLogger(ServiceLocator.class);
	private ServiceLocator() {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-mongo-orm.xml");
		LOGGER.info(context.getBean("mongoConfig"));
	}

	public static ServiceLocator getInstance() {
		return serviceLocator;
	}
}
