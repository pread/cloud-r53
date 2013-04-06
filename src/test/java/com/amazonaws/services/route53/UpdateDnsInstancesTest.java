package com.amazonaws.services.route53;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.route53.config.AppConfig;
import com.amazonaws.services.route53.model.*;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Phillip Read
 */
public class UpdateDnsInstancesTest {

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm");
    private static final String DOMAIN = "tangiblecloud.eu.";
	
	@Before
	public void setup() {
    	ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
    	ec2 = ctx.getBean(AmazonEC2.class);
    	route53 = ctx.getBean(Route53ServiceImpl.class);
    	parser = new SpelExpressionParser();
	}
	
    /*
     * Important: Be sure to fill in your AWS access credentials in the
     *            AwsCredentials.properties file before you try to run this
     *            sample.
     * http://aws.amazon.com/security-credentials
     */
    static AmazonEC2 ec2;
    
	static Route53Service route53;

    static ExpressionParser parser;
    
    @SuppressWarnings("unchecked")
	private ChangeResourceRecordSetsResponse addResourceRecord(String shortName, String publicDns) {
	    
    	StandardEvaluationContext context1 = new StandardEvaluationContext(route53.findAll());
		List<HostedZone> matches = (List<HostedZone>) parser.parseExpression("HostedZones.?[Name == '" + DOMAIN + "']").getValue(context1);

		if (matches.isEmpty()) {
			return new ChangeResourceRecordSetsResponse();
		}
		
		String zoneURI = matches.get(0).getId();
		String zone = StringUtils.substringAfter(zoneURI, "/hostedzone/");
		String domain = matches.get(0).getName();
		System.out.println("Updating Domain =  " + domain + ", Zone =  " + zone);
		String name = (new StringBuilder()).append(shortName).append(".").append(domain).toString();

		Integer maxitems = 500;
		ListResourceRecordSetsResponse result = route53.listResourceRecordSets(zone, maxitems);
		
    	StandardEvaluationContext context = new StandardEvaluationContext(result);
		List<ResourceRecordSet> recordMatches = (List<ResourceRecordSet>) parser.parseExpression("ResourceRecordSet.?[Name == '" + name + "']").getValue(context);
		
		ChangeResourceRecordSetsRequest request;
		List<Change> changes = new ArrayList<Change>();
		String comment = "Adding CNAME records for " + domain + " that points to " + name;
		if (recordMatches.isEmpty()) {
			changes.add(getChangeResourceRecord("CREATE", name, "CNAME", "60", publicDns));
		} else {
			
	    	StandardEvaluationContext context3 = new StandardEvaluationContext(recordMatches.get(0));
			List<ResourceRecord> matches3 = (List<ResourceRecord>) parser.parseExpression("ResourceRecords.?[Value == '" + publicDns + "']").getValue(context3);
			
			if (matches3.isEmpty()) {
				ListResourceRecordSetsResponse filtered = route53.listResourceRecordSetsByTypeName(zone, "CNAME", name, 1);
				changes.add(getChangeResourceRecord("DELETE", name, "CNAME", "60", filtered.getResourceRecordSet().get(0).getResourceRecords().get(0).getValue()));	
			} else {
				changes.add(getChangeResourceRecord("DELETE", name, "CNAME", "60", publicDns));	
			}
			changes.add(getChangeResourceRecord("CREATE", name, "CNAME", "60", publicDns));
		}
		ChangeBatch changeBatch = new ChangeBatch(comment, changes);
		changeBatch.setChanges(changes);
		request =  new ChangeResourceRecordSetsRequest(changeBatch);
		
		return route53.changeResourceRecordBatch(zone, request); 
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

	@Test
	public void getUpdateDNSInstancesEC2() throws ParseException {
		
        DescribeInstancesResult result = ec2.describeInstances();
        
        for(Reservation item : result.getReservations()) {
        	
            for(Instance i : item.getInstances()) {

            	StandardEvaluationContext context = new StandardEvaluationContext(i);
				Boolean running = (Boolean) parser.parseExpression("State.Code != 80").getValue(context);
            	
				if (running) {
       	
    				String name = (String) parser.parseExpression("Tags.?[Key == 'Name'][0].Value").getValue(context);
    				String shortName = (String) parser.parseExpression("Tags.?[Key == 'ShortName'].size() > 0 ? Tags.?[Key == 'ShortName'][0].Value : ''").getValue(context);
                	    			
                	System.out.println(DATE_FORMAT.format(i.getLaunchTime()) + " " +
                			           i.getState().getName() + " " +
     			                       i.getInstanceType() + " " + 
                			           i.getInstanceId() + " " +  
                			           shortName + " " + name);                	
                	
                	ChangeResourceRecordSetsResponse dnsUpdateResult = addResourceRecord(shortName, i.getPublicDnsName());
        			System.out.println("Status: " + dnsUpdateResult.getChangeInfo().getStatus() + "\n");       		
            	}
            	
            }
        	
        }
        
	}

}