package github.macrohuang.orm.mongo.config;

import com.mongodb.MongoOptions;

public class MongoConfig extends MongoOptions{
	private String host;
	private int port = 27017;
	private boolean needAuth = false;
	private String userName;
	private String password;

	public boolean isNeedAuth() {
		return needAuth;
	}

	public void setNeedAuth(boolean needAuth) {
		this.needAuth = needAuth;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
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

	@Override
	public String toString() {
		return String.format("{host:%s,port:%d,options:{%s}}", host, port, super.toString());
	}
}
