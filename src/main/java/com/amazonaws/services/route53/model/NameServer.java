package com.amazonaws.services.route53.model;

import org.apache.commons.lang.builder.ToStringBuilder;

public class NameServer {

	private String nameServer;

	public NameServer(String nameServer) {
		super();
		this.nameServer = nameServer;
	}

	public String getNameServer() {
		return nameServer;
	}

	public void setNameServer(String nameServer) {
		this.nameServer = nameServer;
	}

	@Override
	public String toString()
	{
	    return ToStringBuilder.reflectionToString(this);
	}
}
