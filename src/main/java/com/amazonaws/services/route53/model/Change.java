package com.amazonaws.services.route53.model;

import org.apache.commons.lang.builder.ToStringBuilder;

public class Change {

	private String action;
	private ResourceRecordSet resourceRecordSet;

	public Change(String action, ResourceRecordSet resourceRecordSet) {
		super();
		this.action = action;
		this.resourceRecordSet = resourceRecordSet;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public ResourceRecordSet getResourceRecordSet() {
		return resourceRecordSet;
	}

	public void setResourceRecordSet(ResourceRecordSet resourceRecordSet) {
		this.resourceRecordSet = resourceRecordSet;
	}

	@Override
	public String toString()
	{
	    return ToStringBuilder.reflectionToString(this);
	}
}
