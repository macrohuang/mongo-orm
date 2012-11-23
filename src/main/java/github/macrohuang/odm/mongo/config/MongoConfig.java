package github.macrohuang.odm.mongo.config;

import java.util.Set;

import com.mongodb.MongoOptions;

public class MongoConfig extends MongoOptions{
	private String host;
	private int port = 27017;
	private boolean needAuth = false;
	private String username;
	private String password;
	private Set<String> replicaSetSeeds;
	private boolean readSlave = true;

	public Set<String> getReplicaSetSeeds() {
		return replicaSetSeeds;
	}

	public void setReplicaSetSeeds(Set<String> replicaSetSeeds) {
		this.replicaSetSeeds = replicaSetSeeds;
	}

	public boolean isNeedAuth() {
		return needAuth;
	}

	public void setNeedAuth(boolean needAuth) {
		this.needAuth = needAuth;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getPoolSize() {
		return super.connectionsPerHost;
	}

	public void setPoolSize(int poolSize) {
		super.connectionsPerHost = poolSize;
	}

	public int getMaxConnections() {
		return super.threadsAllowedToBlockForConnectionMultiplier * super.connectionsPerHost;
	}

	public void setMaxConnections(int maxConnections) {
		super.threadsAllowedToBlockForConnectionMultiplier = maxConnections / super.connectionsPerHost;
	}

	public void setMaxWaitTime(int maxWaitTime) {
		super.maxWaitTime = maxWaitTime;
	}

	public void setAutoConnectRetry(boolean autoConnectRetry) {
		super.autoConnectRetry = autoConnectRetry;
	}

	public void setmaxAutoConnectRetryTime(long maxAutoConnectRetryTime) {
		super.maxAutoConnectRetryTime = maxAutoConnectRetryTime;
	}

	public boolean isReadSlave() {
		return readSlave;
	}

	public void setReadSlave(boolean readSlave) {
		this.readSlave = readSlave;
	}

	@Override
	public String toString() {
		return "MongoConfig [host=" + host + ", port=" + port + ", needAuth=" + needAuth + ", username=" + username + ", password=" + password
				+ ", replicaSetSeeds=" + replicaSetSeeds + ", readSlave=" + readSlave + "]";
	}
}
