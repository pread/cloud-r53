package com.amazonaws.services.route53.model;

import java.util.List;

public interface ListHostedZonesResponseFluentInterface {
	  public ListHostedZonesResponseFluentInterface hostedZones(List<HostedZone> hostedZones);
	  public ListHostedZonesResponseFluentInterface maxItems(Integer maxItems);
	  public ListHostedZonesResponseFluentInterface isTruncated(Boolean isTruncated);
	  public ListHostedZonesResponseFluentInterface nextMarker(String nextMarker);
	  public ListHostedZonesResponse create();
}

