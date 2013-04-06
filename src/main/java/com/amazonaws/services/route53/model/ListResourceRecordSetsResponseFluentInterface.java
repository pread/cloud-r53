package com.amazonaws.services.route53.model;

import java.util.List;

public interface ListResourceRecordSetsResponseFluentInterface {
	  public ListResourceRecordSetsResponseFluentInterface resourceRecordSet(List<ResourceRecordSet> resourceRecordSet);
	  public ListResourceRecordSetsResponseFluentInterface maxItems(Integer maxItems);
	  public ListResourceRecordSetsResponseFluentInterface nextRecordName(String nextRecordName);
	  public ListResourceRecordSetsResponseFluentInterface nextRecordType(String nextRecordType);
	  public ListResourceRecordSetsResponseFluentInterface isTruncated(Boolean isTruncated);
	  public ListResourceRecordSetsResponse create();
}
