package com.amazonaws.services.rds;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import com.amazonaws.services.rds.model.DBSnapshot;
import com.amazonaws.services.rds.model.DescribeDBSnapshotsResult;
import com.amazonaws.services.route53.config.AppConfig;

/**
 * 
 * @author Phillip Read
 */
public class DescribeDBSnapshotsTest {

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm");

	@Before
	public void setup() {
    	ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
    	rds = ctx.getBean(AmazonRDS.class);
    	parser = new SpelExpressionParser();
	}
	
    /*
     * Important: Be sure to fill in your AWS access credentials in the
     *            AwsCredentials.properties file before you try to run this
     *            sample.
     * http://aws.amazon.com/security-credentials
     */
    static AmazonRDS rds;

    static ExpressionParser parser;
    
	/**
	 * Call the service layer for integration test.
	 * @throws ParseException 
	 * 
	 * @throws Exception
	 *             with any errors.
	 */
	@Test
	public void getDescribeSnapshots() throws ParseException {
	    
		DescribeDBSnapshotsResult result = rds.describeDBSnapshots();
        
        for(DBSnapshot i : result.getDBSnapshots()) {
        	 			
                	System.out.println(DATE_FORMAT.format(i.getSnapshotCreateTime()) + " " +
     			                       i.getAvailabilityZone() + " " +
                			           i.getStatus() + " " +
     			                       i.getEngine() + " " + 
                			           i.getEngineVersion() + " " +
                			           i.getDBInstanceIdentifier() + " " +  
                			           i.getDBSnapshotIdentifier()+ " " +  
                			           i.getAllocatedStorage()+ " " +  
                			           i.getPort() );
        	
        }

	}

}