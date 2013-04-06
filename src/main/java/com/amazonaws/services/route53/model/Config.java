package com.amazonaws.services.route53.model;

import org.apache.commons.lang.builder.ToStringBuilder;

public class Config {

	private String comment;

	public Config(String comment) {
		super();
		this.comment = comment;
	}
	
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public String toString()
	{
	    return ToStringBuilder.reflectionToString(this);
	}
}
