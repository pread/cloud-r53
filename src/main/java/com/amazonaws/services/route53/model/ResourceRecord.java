package com.amazonaws.services.route53.model;

import org.apache.commons.lang.builder.ToStringBuilder;

public class ResourceRecord {

	private String value;

	public ResourceRecord(String value) {
		super();
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString()
	{
	    return ToStringBuilder.reflectionToString(this);
	}
}
