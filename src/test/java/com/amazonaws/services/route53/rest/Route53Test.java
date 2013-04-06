package com.amazonaws.services.route53.rest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.route53.config.AppConfig;
import com.amazonaws.services.route53.model.HostedZone;
import com.amazonaws.services.route53.model.HostedZoneResponse;
import com.amazonaws.services.route53.model.ListHostedZonesResponse;
import com.amazonaws.services.route53.model.ListResourceRecordSetsResponse;

/**
 * 
 * @author Phillip Read
 */
public class Route53Test  {

	Route53Client route53;
	
	@Before
	public void setup() {
    	ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
    	route53 = ctx.getBean(Route53ClientImpl.class);
	}

	@Test
	public void ListResourceRecordSetsByTypeName() {
		
		// the number of entries known to be inserted 
		final int KNOWN_CAPACITY = 3;
		final int INITIAL_CAPACITY = 1 + (KNOWN_CAPACITY + 1) * 4 / 3; 

		Map<String, String> vars =  new HashMap<String, String> (INITIAL_CAPACITY); 
		vars.put ("zone", "Z7C24NBIAZYD1"); 
		vars.put ("type", "CNAME"); 
		vars.put ("name", "authentication1.tangiblecloud.eu.");
		vars.put ("maxitems", "1");
		
		ListResourceRecordSetsResponse result = route53.request("/2010-10-01/hostedzone/{zone}/rrset?type={type}&name={name}&maxitems={maxitems}", 
				                                                HttpMethod.GET, 
				                                                vars, 
				                                                ListResourceRecordSetsResponse.class);
		
		assertNotNull(result);
		assertNotNull(result.getResourceRecordSet());
		assertFalse(result.getResourceRecordSet().isEmpty());
		System.out.println("Test Result (" + vars.get("zone") + 
				           "): " + result.getResourceRecordSet());
	}
			
	/**
	 * Call the service layer for integration test.  
	 */
	@Test
	public void ListResourceRecordSets() {

		Map<String, String> vars = Collections.singletonMap("zone", "Z7C24NBIAZYD1");
		ListResourceRecordSetsResponse result = route53.request("/2010-10-01/hostedzone/{zone}/rrset?maxitems=1", 
				                                                HttpMethod.GET, 
				                                                vars, 
				                                                ListResourceRecordSetsResponse.class);
		
		assertNotNull(result);
		assertNotNull(result.getResourceRecordSet());
		assertFalse(result.getResourceRecordSet().isEmpty());
		System.out.println("Test Result (" + vars.get("zone") + 
				           "): " + result.getResourceRecordSet());
	}
	
	/**
	 * Call the service layer for integration test.
	 */
	@Test
	public void HostedZone() {

		Map<String, String> vars = Collections.singletonMap("id", "Z7C24NBIAZYD1");
		HostedZoneResponse result = route53.request("/2010-10-01/hostedzone/{id}", HttpMethod.GET, vars, HostedZoneResponse.class);
		
		assertNotNull(result);
		assertNotNull(result.getHostedZone());
		assertNotNull(result.getNameServers());
		assertFalse(result.getNameServers().isEmpty());
		System.out.println("AWS NameServers (" + vars.get("id") + "): " + 
				            result.getNameServers());
	}
	
	/**
	 * Call the service layer for integration test.
	 */
	@Test
	public void ListHostedZonesByMarker() {

		Map<String, String> vars = new HashMap<String, String>();
		ListHostedZonesResponse result = route53.request("/2010-10-01/hostedzone?marker=Z7C24NBIAZYD1&maxitems=10", HttpMethod.GET, vars, ListHostedZonesResponse.class);
		
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

		Map<String, String> vars = new HashMap<String, String>();
		ListHostedZonesResponse result = route53.request("/2010-10-01/hostedzone", HttpMethod.GET, vars, ListHostedZonesResponse.class);
		
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

		Map<String, String> vars = new HashMap<String, String>();
		try {
			route53.request("/2010-10-01/hostedzone?marker=AAABBBFGTFSW&maxitems=1", HttpMethod.GET, vars, HostedZone.class);
			fail("Expecting a 400 Bad Request Exception");			
		} catch (HttpClientErrorException e) {
		}
	}
	
}