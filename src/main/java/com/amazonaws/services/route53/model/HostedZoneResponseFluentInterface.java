package com.amazonaws.services.route53.model;

import java.util.List;

public interface HostedZoneResponseFluentInterface {
	  public HostedZoneResponseFluentInterface hostedZone(HostedZone hostedZone);
	  public HostedZoneResponseFluentInterface nameServers(List<NameServer> nameServers);
	  public HostedZoneResponse create();
}

