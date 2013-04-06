package com.amazonaws.services.rds;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.route53.config.AppConfig;

/**
 * 
 * @author Phillip Read
 */
public class DescribeInstancesTest {

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm");
			
	@Before
	public void setup() {
    	ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
    	ec2 = ctx.getBean(AmazonEC2.class);
    	parser = new SpelExpressionParser();
	}
	
    /*
     * Important: Be sure to fill in your AWS access credentials in the
     *            AwsCredentials.properties file before you try to run this
     *            sample.
     * http://aws.amazon.com/security-credentials
     */
    static AmazonEC2 ec2;

    static ExpressionParser parser;

	@Test
	public void getDescribeInstances() throws ParseException {
	    
        DescribeInstancesResult result = ec2.describeInstances();
        
        for(Reservation item : result.getReservations()) {
        	
            for(Instance i : item.getInstances()) {

            	StandardEvaluationContext context = new StandardEvaluationContext(i);
				Boolean running = (Boolean) parser.parseExpression("State.Code != 80").getValue(context);
            	
				if(running) {
       	
    				String name = (String) parser.parseExpression("Tags.?[Key == 'Name'][0].Value").getValue(context);
    				String shortName = (String) parser.parseExpression("Tags.?[Key == 'ShortName'].size() > 0 ? Tags.?[Key == 'ShortName'][0].Value : ''").getValue(context);
                	    			
                	System.out.println(DATE_FORMAT.format(i.getLaunchTime()) + " " +
                			           i.getState().getName() + " " +
     			                       i.getInstanceType() + " " + 
                			           i.getInstanceId() + " " +  
                			           shortName + " " + name);
            	}
            	
            }
        	
        }
        
	}

}