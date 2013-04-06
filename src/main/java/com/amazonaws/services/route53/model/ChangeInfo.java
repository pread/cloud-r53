package com.amazonaws.services.route53.model;

import java.util.Calendar;
import org.apache.commons.lang.builder.ToStringBuilder;
import com.amazonaws.services.util.FluentInterface;

public class ChangeInfo {

	private String id;
	private String status;
	private Calendar submittedAt;

	public static ChangeInfoFluentInterface with() {
	    return FluentInterface.create(
	      new ChangeInfo(), ChangeInfoFluentInterface.class);
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
