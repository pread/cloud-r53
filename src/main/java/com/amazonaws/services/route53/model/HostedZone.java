package com.amazonaws.services.route53.model;

import org.apache.commons.lang.builder.ToStringBuilder;
import com.amazonaws.services.util.FluentInterface;

public class HostedZone {

	private String id;
	private String name;
	private String callerRef;
	private Config config;
	
	public static HostedZoneFluentInterface with() {
	    return FluentInterface.create(
	      new HostedZone(), HostedZoneFluentInterface.class);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCallerRef() {
		return callerRef;
	}

	public void setCallerRef(String callerRef) {
		this.callerRef = callerRef;
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
