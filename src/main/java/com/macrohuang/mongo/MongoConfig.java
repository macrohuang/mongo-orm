package com.macrohuang.mongo;

import com.mongodb.MongoOptions;

public class MongoConfig extends MongoOptions{
	private String host;
	private int port = 27017;

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
