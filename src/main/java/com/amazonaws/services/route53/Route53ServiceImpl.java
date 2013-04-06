package com.amazonaws.services.route53;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.route53.model.Change;
import com.amazonaws.services.route53.model.ChangeBatch;
import com.amazonaws.services.route53.model.ChangeResourceRecordSetsRequest;
import com.amazonaws.services.route53.model.ChangeResourceRecordSetsResponse;
import com.amazonaws.services.route53.model.ChangeResponse;
import com.amazonaws.services.route53.model.CreateHostedZoneRequest;
import com.amazonaws.services.route53.model.CreateHostedZoneResponse;
import com.amazonaws.services.route53.model.HostedZone;
import com.amazonaws.services.route53.model.HostedZoneResponse;
import com.amazonaws.services.route53.model.ListHostedZonesResponse;
import com.amazonaws.services.route53.model.ListResourceRecordSetsResponse;
import com.amazonaws.services.route53.model.ResourceRecord;
import com.amazonaws.services.route53.model.ResourceRecordSet;
import com.amazonaws.services.route53.rest.Route53Client;

@Service
public class Route53ServiceImpl implements Route53Service {

    private static final Logger log = Logger.getLogger(Route53ServiceImpl.class);    
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm");
    
	/** Inject the Route 53 RESTFUL client. */
    @Autowired(required=true)
    //@Qualifier("ec2Client")
	private Route53Client route53;
	
	/** Inject the Amazon EC2 client. */
	@Autowired(required=true)
    @Qualifier("ec2Client")
	private AmazonEC2 ec2;
	
    static ExpressionParser parser;
    
	public Route53ServiceImpl() {
		super();
		parser = new SpelExpressionParser();
	}

	@SuppressWarnings("unchecked")
	public ListResourceRecordSetsResponse listResourceRecordSetsByTypeName(String zone, String type, String name, Integer maxitems) {	

		// the number of entries known to be inserted 
		final int KNOWN_CAPACITY = 4;
		final int INITIAL_CAPACITY = 1 + (KNOWN_CAPACITY + 1) * 4 / 3; 

		Map<String, String> vars =  new HashMap<String, String> (INITIAL_CAPACITY); 
		vars.put ("zone", zone); 
		vars.put ("type", type); 
		vars.put ("name", name);
		vars.put ("maxitems", String.valueOf(maxitems));
		
		/*
		 * Please note that the arguments name and type only specify the sort order of the result set.
		 */
		ListResourceRecordSetsResponse result =  route53.request("/2010-10-01/hostedzone/{zone}/rrset?type={type}&name={name}&maxitems={maxitems}", 
				                                                 HttpMethod.GET, 
				                                                 vars, 
				                                                 ListResourceRecordSetsResponse.class);
		
    	StandardEvaluationContext context = new StandardEvaluationContext(result);
    	List<ResourceRecordSet> matches = (List<ResourceRecordSet>) parser.parseExpression("ResourceRecordSet.?[Name == '" + name + "']").getValue(context);
		
    	if (matches.isEmpty()) {
    		result.setResourceRecordSet(new ArrayList<ResourceRecordSet>());
    	}
		return result;
	}
	
	public ListResourceRecordSetsResponse listResourceRecordSets(String zone, Integer maxitems) {	

		// the number of entries known to be inserted 
		final int KNOWN_CAPACITY = 2;
		final int INITIAL_CAPACITY = 1 + (KNOWN_CAPACITY + 1) * 4 / 3; 
		Map<String, String> vars =  new HashMap<String, String> (INITIAL_CAPACITY); 
		vars.put ("zone", zone); 
		vars.put ("maxitems", String.valueOf(maxitems));
		
		return route53.request("/2010-10-01/hostedzone/{zone}/rrset?maxitems={maxitems}", HttpMethod.GET, vars, ListResourceRecordSetsResponse.class);
	}

	public CreateHostedZoneResponse createHostedZone(CreateHostedZoneRequest request) {		
		Map<String, String> vars = new HashMap<String, String>();
		return route53.request("/2010-10-01/hostedzone", 
				               HttpMethod.POST, request, vars, 
				               CreateHostedZoneResponse.class);
	}

	public ChangeResourceRecordSetsResponse changeResourceRecordBatch(String hostedZoneId, ChangeResourceRecordSetsRequest request) {

		Map<String, String> vars = Collections.singletonMap("zone", hostedZoneId);
		return route53.request("/2010-10-01/hostedzone/{zone}/rrset", 
				               HttpMethod.POST, 
				               request, vars, 
				               ChangeResourceRecordSetsResponse.class);
	}
	
	public ChangeResponse changeStatus(String id) {
		Map<String, String> vars = Collections.singletonMap("idchange", id);
		return route53.request("/2010-10-01/{idchange}", HttpMethod.GET, vars, ChangeResponse.class);
	}
	
	public ListHostedZonesResponse findAll() {
		Map<String, String> vars = new HashMap<String, String>();
		return route53.request("/2010-10-01/hostedzone", HttpMethod.GET, vars, ListHostedZonesResponse.class);
	}
	
	public ListHostedZonesResponse findByMarker(String marker, Integer maxitems) {

		// the number of entries known to be inserted 
		final int KNOWN_CAPACITY = 2;
		final int INITIAL_CAPACITY = 1 + (KNOWN_CAPACITY + 1) * 4 / 3;
		Map<String, String> vars =  new HashMap<String, String> (INITIAL_CAPACITY);
		vars.put ("marker", marker);
		vars.put ("maxitems", String.valueOf(maxitems));

		return route53.request("/2010-10-01/hostedzone?marker={marker}&maxitems={maxitems}", HttpMethod.GET, vars, ListHostedZonesResponse.class);
	}
	
	public HostedZoneResponse retrieveNameServers(String hostedZoneId) {
		Map<String, String> vars = Collections.singletonMap("id", hostedZoneId);
		return route53.request("/2010-10-01/hostedzone/{id}", HttpMethod.GET, vars, HostedZoneResponse.class);
	}

	public void updateDnsRunningInstances(String dnsName) {

        DescribeInstancesResult result = ec2.describeInstances();
        
        for(Reservation item : result.getReservations()) {
        	
            for(Instance i : item.getInstances()) {

            	StandardEvaluationContext context = new StandardEvaluationContext(i);
				Boolean running = (Boolean) parser.parseExpression("State.Code != 80").getValue(context);
				Boolean tagsEmpty = (Boolean) parser.parseExpression("Tags.isEmpty()").getValue(context);
            	
				if (running && !tagsEmpty) {
    				
					String name = (String) parser.parseExpression("Tags.?[Key == 'Name'][0].Value").getValue(context);    				    				
    				String shortName = (String) parser.parseExpression("Tags.?[Key == 'ShortName'].size() > 0 ? Tags.?[Key == 'ShortName'][0].Value : ''").getValue(context);
                	    	
    				log.info(DATE_FORMAT.format(i.getLaunchTime()) + " " +
     			                i.getState().getName() + " " +
		                        i.getInstanceType() + " " + 
    			                i.getInstanceId() + " " +  
    			                shortName + " " + name);               	
                	
                	ChangeResourceRecordSetsResponse dnsUpdateResult = addResourceRecord(dnsName, shortName, i.getPublicDnsName());
        			if (dnsUpdateResult != null && dnsUpdateResult.getChangeInfo() != null) {
        				log.info("Status: " + dnsUpdateResult.getChangeInfo().getStatus() + "\n");
        			}
            	}
            	
            }
        	
        }
	}
	
	public Instance findRunningInstanceByTag(String searchName) {
	  	
		if (StringUtils.isBlank(searchName)) {                   	
			  return null; 					
		}
		
        DescribeInstancesResult result = ec2.describeInstances();
        
        for(Reservation item : result.getReservations()) {
        	
            for(Instance i : item.getInstances()) {

            	StandardEvaluationContext context = new StandardEvaluationContext(i);            	
				Boolean running = (Boolean) parser.parseExpression("State.Code != 80 and Tags.?[Key == 'ShortName'].size() > 0 ? State.Code != 80 and \"" + searchName + 
						"\".equals(Tags.?[Key == 'ShortName'][0].Value) : false").getValue(context);
								
				if (running) {
       	
    				String shortName = (String) parser.parseExpression("Tags.?[Key == 'ShortName'][0].Value").getValue(context);
                	    	
    				if (!StringUtils.isBlank(shortName) && 
    					!StringUtils.isBlank(i.getPublicDnsName())) {                   	
    					  return i;    					
    				}
            	}            	
            }
        	
        }
		return null;		
	}
	
	@SuppressWarnings("unchecked")
	public ChangeResourceRecordSetsResponse addResourceRecord(String dnsName, String shortName, String publicDns) {
	    
		if (StringUtils.isBlank(dnsName) ||	StringUtils.isBlank(shortName) || StringUtils.isBlank(publicDns)) {
			   return null;
		}
		
    	StandardEvaluationContext context1 = new StandardEvaluationContext(findAll());
		List<HostedZone> matches = (List<HostedZone>) parser.parseExpression("HostedZones.?[Name == '" + dnsName + "']").getValue(context1);

		if (matches.isEmpty()) {
			return new ChangeResourceRecordSetsResponse();
		}

		ChangeResourceRecordSetsRequest request;
		List<Change> changes = new ArrayList<Change>();
		String zoneURI = matches.get(0).getId();
		String zone = StringUtils.substringAfter(zoneURI, "/hostedzone/");
		String domain = matches.get(0).getName();
		String name = (new StringBuilder()).append(shortName).append(".").append(domain).toString();
		
		String value = null;
		ListResourceRecordSetsResponse filtered = listResourceRecordSetsByTypeName(zone, "CNAME", name, 1);
		if (filtered.getResourceRecordSet().isEmpty()) {
			changes.add(getChangeResourceRecord("CREATE", name, "CNAME", "60", publicDns));
		} else {
			value = filtered.getResourceRecordSet().get(0).getResourceRecords().get(0).getValue();
			if (publicDns.equals(value)) {
				return new ChangeResourceRecordSetsResponse();
			}
		}
		String comment = "Adding CNAME records for " + domain + " that points to " + name;
		if (value != null) {
			changes.add(getChangeResourceRecord("DELETE", name, "CNAME", "60", value));	
			changes.add(getChangeResourceRecord("CREATE", name, "CNAME", "60", publicDns));
		}

		ChangeBatch changeBatch = new ChangeBatch(comment, changes);
		changeBatch.setChanges(changes);
		request =  new ChangeResourceRecordSetsRequest(changeBatch);

		log.info("Updating Domain =  " + domain + ", Zone =  " + zone);
		return changeResourceRecordBatch(zone, request); 
    }
	
    private Change getChangeResourceRecord(String action, String name, String type, String ttl, String publicDns) {
    	
		ResourceRecord record = new ResourceRecord(publicDns);
		List <ResourceRecord> resourceRecords = new ArrayList<ResourceRecord>();
		
		resourceRecords.add(record);
		ResourceRecordSet resourceRecordSet = 
			ResourceRecordSet.with()
            .name(name)
            .type(type)
            .ttl(ttl)
            .resourceRecords(resourceRecords)
            .create();
		
		return new Change(action, resourceRecordSet);
    }
	
}
