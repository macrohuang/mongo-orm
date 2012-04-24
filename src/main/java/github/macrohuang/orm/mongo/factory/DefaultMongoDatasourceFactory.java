package github.macrohuang.orm.mongo.factory;

import github.macrohuang.orm.mongo.config.MongoConfig;

public class DefaultMongoDatasourceFactory extends AbstractMongoDatasourceFactory {

	public DefaultMongoDatasourceFactory() {
	}
	public DefaultMongoDatasourceFactory(MongoConfig config) {
		this.config = config;
	}

	@Override
	public void setConfig(MongoConfig config) {
		super.config = config;
	}
}
