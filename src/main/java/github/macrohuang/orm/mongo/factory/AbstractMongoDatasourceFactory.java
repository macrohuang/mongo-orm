package github.macrohuang.orm.mongo.factory;

import github.macrohuang.orm.mongo.config.MongoConfig;
import github.macrohuang.orm.mongo.exception.MongoDatasourceException;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

import com.mongodb.Mongo;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;

public abstract class AbstractMongoDatasourceFactory implements InitializingBean {
	protected MongoConfig config;
	private Mongo mongo;
	private Set<String> include;
	private static final Logger logger = Logger.getLogger(AbstractMongoDatasourceFactory.class);

	public AbstractMongoDatasourceFactory() {
	}

	public abstract void setConfig(MongoConfig config);

	public Set<String> getInclude() {
		return include;
	}

	public void setInclude(Set<String> include) {
		this.include = include;
	}

	private List<ServerAddress> getServerAddresses(Set<String> replicaSetSeeds) {
		List<ServerAddress> serverAddresses = new ArrayList<ServerAddress>();
		for (String replicaSet : replicaSetSeeds) {
			try {
				serverAddresses.add(new ServerAddress(replicaSet));
			} catch (UnknownHostException e) {
				e.printStackTrace();
				throw new MongoDatasourceException("can not connect to the server", e);
			}
		}
		return serverAddresses;
	}

	public Mongo getMongoDatasource() throws MongoDatasourceException {
		return mongo;
	}

	public MongoConfig getConfig() {
		return config;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (config.getReplicaSetSeeds() != null && config.getReplicaSetSeeds().size() > 0) {// Replcate
			// set.
			logger.info("it's a replecate set config" + config.getReplicaSetSeeds());
			List<ServerAddress> serverAddresses = getServerAddresses(config.getReplicaSetSeeds());
			mongo = new Mongo(serverAddresses, config);
			if (config.isReadSlave()) {// 20120529,Read from secondary,default
										// true.
				mongo.setReadPreference(ReadPreference.SECONDARY);
			}
		} else {
			logger.info("it's single server config" + config.getHost() + ":" + config.getPort());
			mongo = new Mongo(new ServerAddress(config.getHost(), config.getPort()), config);
		}
	}

	public void destory() {
		if (mongo != null)
			mongo.close();
	}
}
