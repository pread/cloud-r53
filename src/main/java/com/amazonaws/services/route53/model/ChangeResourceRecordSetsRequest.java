package com.amazonaws.services.route53.model;

import org.apache.commons.lang.builder.ToStringBuilder;

public class ChangeResourceRecordSetsRequest {

	private ChangeBatch changeBatch;
		
	public ChangeResourceRecordSetsRequest(ChangeBatch changeBatch) {
		super();
		this.changeBatch = changeBatch;
	}

	public ChangeBatch getChangeBatch() {
		return changeBatch;
	}

	public void setChangeBatch(ChangeBatch changeBatch) {
		this.changeBatch = changeBatch;
	}

	@Override
	public String toString()
	{
	    return ToStringBuilder.reflectionToString(this);
	}
	
}
