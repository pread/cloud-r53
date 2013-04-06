package com.amazonaws.services.route53.model;

import java.util.List;
import org.apache.commons.lang.builder.ToStringBuilder;
import com.amazonaws.services.util.FluentInterface;

public class ListHostedZonesResponse {

	private List<HostedZone> hostedZones;
	private Integer maxItems;
	private Boolean isTruncated;
	private String nextMarker;
	
	public static ListHostedZonesResponseFluentInterface with() {
	    return FluentInterface.create(
	      new ListHostedZonesResponse(), ListHostedZonesResponseFluentInterface.class);
	}

	public List<HostedZone> getHostedZones() {
		return hostedZones;
	}

	public void setHostedZones(List<HostedZone> hostedZones) {
		this.hostedZones = hostedZones;
	}

	public Integer getMaxItems() {
		return maxItems;
	}

	public void setMaxItems(Integer maxItems) {
		this.maxItems = maxItems;
	}

	public Boolean getIsTruncated() {
		return isTruncated;
	}

	public void setIsTruncated(Boolean isTruncated) {
		this.isTruncated = isTruncated;
	}

	public String getNextMarker() {
		return nextMarker;
	}

	public void setNextMarker(String nextMarker) {
		this.nextMarker = nextMarker;
	}
	
	@Override
	public String toString()
	{
	    return ToStringBuilder.reflectionToString(this);
	}
	
}
