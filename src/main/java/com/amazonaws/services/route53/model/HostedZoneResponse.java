package com.amazonaws.services.route53.model;

import java.util.List;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.amazonaws.services.util.FluentInterface;

public class HostedZoneResponse {

	private HostedZone hostedZone;
	private List<NameServer> nameServers;
	
	public static HostedZoneResponseFluentInterface with() {
	    return FluentInterface.create(
	      new HostedZoneResponse(), HostedZoneResponseFluentInterface.class);
	}

	public HostedZone getHostedZone() {
		return hostedZone;
	}

	public void setHostedZone(HostedZone hostedZone) {
		this.hostedZone = hostedZone;
	}

	public List<NameServer> getNameServers() {
		return nameServers;
	}

	public void setNameServers(List<NameServer> nameServers) {
		this.nameServers = nameServers;
	}

	@Override
	public String toString()
	{
	    return ToStringBuilder.reflectionToString(this);
	}
	
}
