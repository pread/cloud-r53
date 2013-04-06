package com.amazonaws.services.route53.model;

import java.util.List;

public interface ResourceRecordSetFluentInterface {
	  public ResourceRecordSetFluentInterface name(String name);
	  public ResourceRecordSetFluentInterface type(String type);
	  public ResourceRecordSetFluentInterface ttl(String ttl);
	  public ResourceRecordSetFluentInterface resourceRecords(List <ResourceRecord> resourceRecords);
	  public ResourceRecordSet create();
}
