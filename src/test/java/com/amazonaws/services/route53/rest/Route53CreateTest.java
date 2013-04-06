package com.amazonaws.services.route53.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.HttpMethod;

import com.amazonaws.services.route53.config.AppConfig;
import com.amazonaws.services.route53.model.Change;
import com.amazonaws.services.route53.model.ChangeBatch;
import com.amazonaws.services.route53.model.ChangeResourceRecordSetsRequest;
import com.amazonaws.services.route53.model.ChangeResourceRecordSetsResponse;
import com.amazonaws.services.route53.model.ChangeResponse;
import com.amazonaws.services.route53.model.CreateHostedZoneRequest;
import com.amazonaws.services.route53.model.CreateHostedZoneResponse;
import com.amazonaws.services.route53.model.ResourceRecord;
import com.amazonaws.services.route53.model.ResourceRecordSet;
import com.amazonaws.services.util.WatchUtils;

/**
 * 
 * @author Phillip Read
 */
public class Route53CreateTest  {
	
	private static final Format TIMESTAMP_FORMAT = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z");
	
	Route53Client route53;
	
	@Before
	public void setup() {
    	ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
    	route53 = ctx.getBean(Route53ClientImpl.class);
	}

	/**
	 * Call the service layer for integration test.  
	 */
	public void changeResourceRecordSetsRequest(String domain, String zone) {

		WatchUtils.resetAllTasks();
		
		String comment = "Adding A records for tangiblecloud.eu and a CNAME record for enttstapp1.tangiblecloud.eu that points to tangiblecloud.eu.";		
		String action = "CREATE";
		String name = "enttstapp1.".concat(domain);
		String type = "CNAME";
		String ttl = "60";
		
		ResourceRecord record = new ResourceRecord("ec2-46-51-131-248.eu-west-1.compute.amazonaws.com");
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
		
		Map<String, String> vars = Collections.singletonMap("zone", zone);	
		
	    WatchUtils.startTask("time taken for requesting to add a new CNAME record to the domain.");
		ChangeResourceRecordSetsResponse result = route53.request("/2010-10-01/hostedzone/{zone}/rrset", 
				                                          HttpMethod.POST, 
				                                          request, vars, 
				                                          ChangeResourceRecordSetsResponse.class);
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
			changeStatus = changeStatus(result.getChangeInfo().getId());
			WatchUtils.endTask();
			assertNotNull(changeStatus);
			assertNotNull(changeStatus.getChangeInfo());
			pending = "PENDING".equals(changeStatus.getChangeInfo().getStatus());
			i++;
		}
		
		assertEquals("INSYNC", changeStatus.getChangeInfo().getStatus());
		
		System.out.println("Id: " + changeStatus.getChangeInfo().getId());
		System.out.println("Status: " + changeStatus.getChangeInfo().getStatus());
		System.out.println("SubmittedAt: " + TIMESTAMP_FORMAT.format(changeStatus.getChangeInfo().getSubmittedAt().getTime()));

		System.out.println(WatchUtils.getTaskSummary());
		
	}	
	
	/**
	 * Call the service layer for integration test.  
	 */
	@Test
	public void changeResourceRecordSetRequest() {

		String token = getToken();
		CreateHostedZoneRequest request = new CreateHostedZoneRequest("mydomain" + token + ".com.", 
				                                                      token, 
		                                                              "My first hosted zone comment");
		Map<String, String> vars = new HashMap<String, String>();
		CreateHostedZoneResponse result = route53.request("/2010-10-01/hostedzone", 
				                                          HttpMethod.POST, 
				                                          request, vars, 
				                                          CreateHostedZoneResponse.class);
		
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
			changeStatus = changeStatus(result.getChangeInfo().getId());
			assertNotNull(changeStatus);
			assertNotNull(changeStatus.getChangeInfo());
			pending = "PENDING".equals(changeStatus.getChangeInfo().getStatus());
			i++;
		}
		assertEquals("INSYNC", changeStatus.getChangeInfo().getStatus());
		System.out.println("Successful: PENDING status changed to INSYNC after " + i + " attempts!");
		
		String zoneURI = result.getHostedZone().getId();
		String zone = StringUtils.substringAfter(zoneURI, "/hostedzone/");
		String domain = result.getHostedZone().getName();
		changeResourceRecordSetsRequest(domain, zone);
		
	}
	
	/**
	 * Call the service layer for integration test.
    */
	public ChangeResponse changeStatus(String id) {
		Map<String, String> vars = Collections.singletonMap("changeId", id);
		return route53.request("/2010-10-01/{changeId}", HttpMethod.GET, vars, ChangeResponse.class);
	}

	private static String getToken() {
		return Long.toString(Math.abs((new Random()).nextLong()), 36);
	}
	
}