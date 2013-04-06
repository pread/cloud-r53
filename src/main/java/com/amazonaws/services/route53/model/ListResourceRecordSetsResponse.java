package com.amazonaws.services.route53.model;

import java.util.List;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.amazonaws.services.util.FluentInterface;

public class ListResourceRecordSetsResponse {

	private List<ResourceRecordSet> resourceRecordSet;
	private Integer maxItems;
	private String nextRecordName;
	private String nextRecordType;
	private Boolean isTruncated;

	public static ListResourceRecordSetsResponseFluentInterface with() {
	    return FluentInterface.create(
	      new ListResourceRecordSetsResponse(), ListResourceRecordSetsResponseFluentInterface.class);
	}

	public List<ResourceRecordSet> getResourceRecordSet() {
		return resourceRecordSet;
	}

	public void setResourceRecordSet(List<ResourceRecordSet> resourceRecordSet) {
		this.resourceRecordSet = resourceRecordSet;
	}

	public Integer getMaxItems() {
		return maxItems;
	}

	public void setMaxItems(Integer maxItems) {
		this.maxItems = maxItems;
	}

	public String getNextRecordName() {
		return nextRecordName;
	}

	public void setNextRecordName(String nextRecordName) {
		this.nextRecordName = nextRecordName;
	}

	public String getNextRecordType() {
		return nextRecordType;
	}

	public void setNextRecordType(String nextRecordType) {
		this.nextRecordType = nextRecordType;
	}

	public Boolean getIsTruncated() {
		return isTruncated;
	}

	public void setIsTruncated(Boolean isTruncated) {
		this.isTruncated = isTruncated;
	}

	@Override
	public String toString()
	{
	    return ToStringBuilder.reflectionToString(this);
	}
	
}
