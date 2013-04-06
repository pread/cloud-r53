package com.amazonaws.services.route53.model;

public interface HostedZoneFluentInterface {
	  public HostedZoneFluentInterface id(String id);
	  public HostedZoneFluentInterface name(String name);
	  public HostedZoneFluentInterface callerRef(String callerRef);
	  public HostedZoneFluentInterface config(Config config);
	  public HostedZone create();
}

