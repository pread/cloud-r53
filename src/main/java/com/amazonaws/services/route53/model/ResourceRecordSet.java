package com.amazonaws.services.route53.model;

import java.util.List;
import org.apache.commons.lang.builder.ToStringBuilder;
import com.amazonaws.services.util.FluentInterface;

public class ResourceRecordSet {

	private String name;
	private String type;
	private String ttl;
	private List <ResourceRecord> resourceRecords;
	
	public static ResourceRecordSetFluentInterface with() {
	    return FluentInterface.create(
	      new ResourceRecordSet(), ResourceRecordSetFluentInterface.class);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTtl() {
		return ttl;
	}

	public void setTtl(String ttl) {
		this.ttl = ttl;
	}

	public List<ResourceRecord> getResourceRecords() {
		return resourceRecords;
	}

	public void setResourceRecords(List<ResourceRecord> resourceRecords) {
		this.resourceRecords = resourceRecords;
	}
	
	@Override
	public String toString()
	{
	    return ToStringBuilder.reflectionToString(this);
	}

}
