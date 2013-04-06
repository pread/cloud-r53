package com.amazonaws.services.route53.model;

import java.util.List;
import org.apache.commons.lang.builder.ToStringBuilder;

public class CreateHostedZoneResponse {

	private HostedZone hostedZone;
	private ChangeInfo changeInfo;
	private List<NameServer> nameServers;
		
	public CreateHostedZoneResponse(HostedZone hostedZone, ChangeInfo changeInfo, List<NameServer> nameServers) {
		super();
		this.hostedZone = hostedZone;
		this.changeInfo = changeInfo;
		this.nameServers = nameServers;
	}

	public HostedZone getHostedZone() {
		return hostedZone;
	}

	public void setHostedZone(HostedZone hostedZone) {
		this.hostedZone = hostedZone;
	}

	public ChangeInfo getChangeInfo() {
		return changeInfo;
	}

	public void setChangeInfo(ChangeInfo changeInfo) {
		this.changeInfo = changeInfo;
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
