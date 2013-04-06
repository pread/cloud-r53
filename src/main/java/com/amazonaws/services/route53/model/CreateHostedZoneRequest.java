package com.amazonaws.services.route53.model;

import org.apache.commons.lang.builder.ToStringBuilder;

public class CreateHostedZoneRequest {

	private String name;
	private String callerReference;
	private Config config;

	public CreateHostedZoneRequest(String name, String callerReference) {
		super();
		this.name = name;
		this.callerReference = callerReference;
	}
	
	public CreateHostedZoneRequest(String name, String callerReference, String comment) {
		super();
		this.name = name;
		this.callerReference = callerReference;
		this.config = new Config(comment);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCallerReference() {
		return callerReference;
	}

	public void setCallerReference(String callerReference) {
		this.callerReference = callerReference;
	}

	public Config getConfig() {
		return config;
	}

	public void setConfig(Config config) {
		this.config = config;
	}

	@Override
	public String toString()
	{
	    return ToStringBuilder.reflectionToString(this);
	}
}
