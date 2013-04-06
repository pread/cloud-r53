package com.amazonaws.services.route53;

import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.route53.model.ChangeResourceRecordSetsRequest;
import com.amazonaws.services.route53.model.ChangeResourceRecordSetsResponse;
import com.amazonaws.services.route53.model.CreateHostedZoneRequest;
import com.amazonaws.services.route53.model.CreateHostedZoneResponse;
import com.amazonaws.services.route53.model.HostedZoneResponse;
import com.amazonaws.services.route53.model.ListHostedZonesResponse;
import com.amazonaws.services.route53.model.ChangeResponse;
import com.amazonaws.services.route53.model.ListResourceRecordSetsResponse;

public interface Route53Service {

	void updateDnsRunningInstances(String domain);
	Instance findRunningInstanceByTag(String shortName);
	ChangeResourceRecordSetsResponse addResourceRecord(String dnsName, String shortName, String dnsValue);

	ListResourceRecordSetsResponse listResourceRecordSets(String zone, Integer maxitems);
	ListResourceRecordSetsResponse listResourceRecordSetsByTypeName(String zone, String type, String name, Integer maxitems);
	CreateHostedZoneResponse createHostedZone(CreateHostedZoneRequest request);
	ChangeResourceRecordSetsResponse changeResourceRecordBatch(String hostedZoneId, ChangeResourceRecordSetsRequest request);
	ChangeResponse changeStatus(String id);
	ListHostedZonesResponse findAll();
	ListHostedZonesResponse findByMarker(String marker, Integer maxitems);
	HostedZoneResponse retrieveNameServers(String hostedZoneId);

}
