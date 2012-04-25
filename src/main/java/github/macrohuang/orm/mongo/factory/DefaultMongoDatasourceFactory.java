package github.macrohuang.orm.mongo.factory;

import github.macrohuang.orm.mongo.config.MongoConfig;

import org.apache.log4j.Logger;

public class DefaultMongoDatasourceFactory extends AbstractMongoDatasourceFactory {
	private static final Logger logger = Logger.getLogger(DefaultMongoDatasourceFactory.class);
	public DefaultMongoDatasourceFactory() {
	}
	public DefaultMongoDatasourceFactory(MongoConfig config) {
		this.config = config;
	}

	@Override
	public void setConfig(MongoConfig config) {
		logger.info("config is setted to :" + config);
		super.config = config;
	}
}
