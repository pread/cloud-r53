package com.amazonaws.services.route53;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.amazonaws.services.route53.model.*;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.client.HttpClientErrorException;

import com.amazonaws.services.route53.config.AppConfig;
import com.amazonaws.services.util.WatchUtils;

/**
 * 
 * @author Phillip Read
 */
public class Route53ServicesTest  {

    private static final String AWS_PUBLIC_DNS = "ec2-46-51-131-248.eu-west-1.compute.amazonaws.com";
	private static final Format TIMESTAMP_FORMAT = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z");
	
	static Route53Service route53;

	@Before
	public void setup() {
    	ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
    	route53 = ctx.getBean(Route53ServiceImpl.class);
	}

	/**
	 * Call the service layer for integration test.  
	 */
	@Test
	public void updateDnsRunningInstancesTest() {
		
		route53.updateDnsRunningInstances("tangiblecloud.eu.");
	}

	/**
	 * Call the service layer for integration test.  
	 */
	@Test
	public void changeResourceRecordBatch() {

		WatchUtils.resetAllTasks();
		
		CreateHostedZoneResponse hostedZone = createHostedZone();

		String zoneURI = hostedZone.getHostedZone().getId();
		String zone = StringUtils.substringAfter(zoneURI, "/hostedzone/");
		String domain = hostedZone.getHostedZone().getName();
		System.out.println("Domain =  " + domain + ", Zone =  " + zone);

		String comment = "Adding A records for tangiblecloud.eu and a CNAME record for enttstapp1.tangiblecloud.eu that points to tangiblecloud.eu.";		
		String action = "CREATE";
		String name = "enttstapp1.".concat(domain);
		String type = "CNAME";
		String ttl = "60";
		
		ResourceRecord record = new ResourceRecord(AWS_PUBLIC_DNS);
		List <ResourceRecord> resourceRecords = new ArrayList<ResourceRecord>();
		resourceRecords.add(record);
		ResourceRecordSet resourceRecordSet = 
			ResourceRecordSet.with()
            .name(name)
            .type(type)
            .ttl(ttl)
            .resourceRecords(resourceRecords)
            .create();
		
		Change change = new Change(action, resourceRecordSet);
		List<Change> changes = new ArrayList<Change>();
		changes.add(change);
		ChangeBatch changeBatch = new ChangeBatch(comment, changes);
		changeBatch.setChanges(changes);
		ChangeResourceRecordSetsRequest request = new ChangeResourceRecordSetsRequest(changeBatch);
		System.out.println("ChangeResourceRecordSetsRequest: " + request);
		
	    WatchUtils.startTask("time taken for requesting to add a new CNAME record to the domain.");
		ChangeResourceRecordSetsResponse result = route53.changeResourceRecordBatch(zone, request);
		WatchUtils.endTask();

		assertNotNull(result);
		assertNotNull(result.getChangeInfo());
		
		ChangeResponse changeStatus = null;
		boolean pending = true;
		int i = 1;
		while (i<101 && pending) {
			if (i == 100) {
				fail("PENDING status did not change to INSYNC after " + i + " attempts!");
			}

		    WatchUtils.startTask("#" + i + ": time taken for checking the status of the request.");

			changeStatus = route53.changeStatus(result.getChangeInfo().getId());
			WatchUtils.endTask();
			assertNotNull(changeStatus);
			assertNotNull(changeStatus.getChangeInfo());
			pending = "PENDING".equals(changeStatus.getChangeInfo().getStatus());
			i++;
		}
        assertNotNull(changeStatus.getChangeInfo().getStatus());
		assertEquals("INSYNC", changeStatus.getChangeInfo().getStatus());
		
		System.out.println("Id: " + changeStatus.getChangeInfo().getId());
		System.out.println("Status: " + changeStatus.getChangeInfo().getStatus());
		System.out.println("SubmittedAt: " + TIMESTAMP_FORMAT.format(changeStatus.getChangeInfo().getSubmittedAt().getTime()));

		System.out.println(WatchUtils.getTaskSummary());
	}
	
	CreateHostedZoneResponse createHostedZone() {

		String token = getToken();
		CreateHostedZoneRequest request = new CreateHostedZoneRequest("mydomain" + token + ".com.", 
				                                                      token, 
				                                                      "My first hosted zone comment");
		CreateHostedZoneResponse result = route53.createHostedZone(request);
		
		assertNotNull(result);
		assertNotNull(result.getChangeInfo());
		assertNotNull(result.getHostedZone());
		assertNotNull(result.getNameServers());
		assertFalse(result.getNameServers().isEmpty());
		
		ChangeResponse changeStatus = null;
		boolean pending = true;
		int i = 1;
		while (i<201 && pending) {
			if (i == 200) {
				fail("PENDING status did not change to INSYNC after " + i + " attempts!");
			}
			changeStatus = route53.changeStatus(result.getChangeInfo().getId());
			assertNotNull(changeStatus);
			assertNotNull(changeStatus.getChangeInfo());
			pending = "PENDING".equals(changeStatus.getChangeInfo().getStatus());
			i++;
		}
		assertEquals("INSYNC", changeStatus.getChangeInfo().getStatus());
		System.out.println("Successful: PENDING status changed to INSYNC after " + i + " attempts!");
		
		return result;
	}
	
	/**
	 * Call the service layer for integration test.  
	 */
	@Test
	public void CreateHostedZone() {
		CreateHostedZoneResponse hostedZone = createHostedZone();

		String zoneURI = hostedZone.getHostedZone().getId();
		String zone = StringUtils.substringAfter(zoneURI, "/hostedzone/");
		String domain = hostedZone.getHostedZone().getName();
		System.out.println("Domain =  " + domain + ", Zone =  " + zone);		
	}
	
	/**
	 * Call the service layer for integration test.  
	 */
	@Test
	public void ListResourceRecordSets() {

		String zone = "Z7C24NBIAZYD1";
		Integer maxitems = 5;
		ListResourceRecordSetsResponse result = route53.listResourceRecordSets(zone, maxitems);

		assertNotNull(result);
		assertNotNull(result.getResourceRecordSet());
		assertFalse(result.getResourceRecordSet().isEmpty());
		
		for(ResourceRecordSet i : result.getResourceRecordSet()) {
			assertNotNull(i);
			assertNotNull(i.getResourceRecords());
			assertFalse(i.getResourceRecords().isEmpty());
		}
		System.out.println("AWS ResourceRecordSets: " + result);
	}
	
	/**
	 * Call the service layer for integration test.
	 */
	@Test
	public void ListNameServers() {

		String hostedZoneId = "Z7C24NBIAZYD1";
		HostedZoneResponse result = route53.retrieveNameServers(hostedZoneId);
		
		assertNotNull(result);
		assertNotNull(result.getHostedZone());
		assertNotNull(result.getNameServers());
		assertFalse(result.getNameServers().isEmpty());
		System.out.println("AWS NameServers (Z7C24NBIAZYD1): " + result.getNameServers());
	}
	
	/**
	 * Call the service layer for integration test.
	 */
	@Test
	public void ListHostedZonesByMarker() {

		String marker = "Z7C24NBIAZYD1";
		Integer maxitems = 10;
		ListHostedZonesResponse result = route53.findByMarker(marker, maxitems);

		assertNotNull(result);
		assertNotNull(result.getHostedZones());
		assertFalse(result.getHostedZones().isEmpty());
		System.out.println("Test Result (Z7C24NBIAZYD1): " + result.getHostedZones());
	}
	
	/**
	 * Call the service layer for integration test.
	 */
	@Test
	public void ListAllHostedZones() {

		ListHostedZonesResponse result = route53.findAll();
		assertNotNull(result);
		assertNotNull(result.getHostedZones());
		assertFalse(result.getHostedZones().isEmpty());
		System.out.println("Test Result (All HostedZones): " + result.getHostedZones());
	}
	
	/**
	 * Call the service layer for integration test.
	 */
	@Test
	public void ListHostedZonesError() {

		try {
			route53.findByMarker("Z2RTYAWTGCTBG2", 1);
			fail("Expecting a 400 Bad Request Exception");			
		} catch (HttpClientErrorException e) {
			System.out.println("Test Result: " + e);
		}
	}
	
	private static String getToken() {
		return Long.toString(Math.abs((new Random()).nextLong()), 36);
	}
	
}