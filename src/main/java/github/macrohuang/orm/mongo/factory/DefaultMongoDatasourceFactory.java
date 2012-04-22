package github.macrohuang.orm.mongo.factory;

import github.macrohuang.orm.mongo.config.MongoConfig;

public class DefaultMongoDatasourceFactory extends AbstractMongoDatasourceFactory {

	public DefaultMongoDatasourceFactory() {
		super(new MongoConfig());
	}
	public DefaultMongoDatasourceFactory(MongoConfig config) {
		super(config);
	}
}
