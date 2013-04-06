package com.amazonaws.services.route53.scripts;

import java.text.Format;
import java.text.SimpleDateFormat;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.route53.Route53Service;
import com.amazonaws.services.route53.Route53ServiceImpl;
import com.amazonaws.services.route53.config.AppConfig;
import com.amazonaws.services.route53.model.ChangeResourceRecordSetsResponse;

public class CloudR53 {

	private static final Format TIMESTAMP_FORMAT = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z");
	
	private static final String DNS_NAME = "tangiblecloud.eu.";
	private static final String WILDCARD_DNS = "\\052";
	private static final String CLOUDFOUNDRY_TAG = "cloudfoundry1";
	
	private static Route53Service route53;
    
	/*
	 * Arguments:
	 * 
	 * DNS Name: 
	 * WildCard DNS Tag:
	 * Access Key:
	 * Secret Key:
	 */
    public static void main(String[] args) {

    	ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
    	route53 = ctx.getBean(Route53ServiceImpl.class);
    	
    	/* Update DNS for all RUNNING instances setting dns name using instance ShortName tag. */
		route53.updateDnsRunningInstances(DNS_NAME);

    	/* Find the Cloud Foundry RUNNING instance. Add the matching wild card dns resource. */
    	Instance instance = route53.findRunningInstanceByTag(CLOUDFOUNDRY_TAG);
    	if (instance != null) {
    		ChangeResourceRecordSetsResponse response = route53.addResourceRecord(DNS_NAME, WILDCARD_DNS, instance.getPublicDnsName());
    		if (response.getChangeInfo() != null) {
        		System.out.println("\n\nUpdating Wildcard DNS ... ");
        		System.out.println("Id: " + response.getChangeInfo().getId());
        		System.out.println("Status: " + response.getChangeInfo().getStatus());
        		System.out.println("SubmittedAt: " + TIMESTAMP_FORMAT.format(response.getChangeInfo().getSubmittedAt().getTime()));
    		}
    	}		
    }

}
