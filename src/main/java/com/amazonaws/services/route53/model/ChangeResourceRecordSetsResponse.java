package com.amazonaws.services.route53.model;

import org.apache.commons.lang.builder.ToStringBuilder;

public class ChangeResourceRecordSetsResponse {

	private ChangeInfo changeInfo;

	public ChangeResourceRecordSetsResponse(ChangeInfo changeInfo) {
		super();
		this.changeInfo = changeInfo;
	}
	
	public ChangeResourceRecordSetsResponse() {
		super();
	}

	public ChangeInfo getChangeInfo() {
		return changeInfo;
	}

	public void setChangeInfo(ChangeInfo changeInfo) {
		this.changeInfo = changeInfo;
	}

	@Override
	public String toString()
	{
	    return ToStringBuilder.reflectionToString(this);
	}
	
}
