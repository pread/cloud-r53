package com.amazonaws.services.route53.model;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import com.amazonaws.services.route53.model.Change;

public class ChangeBatch {

	private String comment;
	private List<Change> changes;

	public ChangeBatch(String comment, List<Change> changes) {
		super();
		this.comment = comment;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public List<Change> getChanges() {
		return changes;
	}

	public void setChanges(List<Change> changes) {
		this.changes = changes;
	}

	@Override
	public String toString()
	{
	    return ToStringBuilder.reflectionToString(this);
	}
}
