package com.amazonaws.services.route53.model;

import java.util.Calendar;
import org.apache.commons.lang.builder.ToStringBuilder;

public class DeleteHostedZoneResponse {

	private String id;
	private String status;
	private Calendar submittedAt;

	public DeleteHostedZoneResponse(String id, String status, Calendar submittedAt) {
		super();
		this.id = id;
		this.status = status;
		this.submittedAt = submittedAt;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Calendar getSubmittedAt() {
		return submittedAt;
	}

	public void setSubmittedAt(Calendar submittedAt) {
		this.submittedAt = submittedAt;
	}

	@Override
	public String toString()
	{
	    return ToStringBuilder.reflectionToString(this);
	}
}
