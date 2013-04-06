package com.amazonaws.services.rds;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import com.amazonaws.services.rds.model.DBInstance;
import com.amazonaws.services.rds.model.DBParameterGroupStatus;
import com.amazonaws.services.rds.model.DBSecurityGroupMembership;
import com.amazonaws.services.rds.model.DescribeDBInstancesResult;
import com.amazonaws.services.route53.config.AppConfig;

/**
 * 
 * @author Phillip Read
 */
public class DescribeDBInstancesTest {

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
	public void getDescribeDBInstances() throws ParseException {

	   DescribeDBInstancesResult result = rds.describeDBInstances();
      /*
	   AuthorizeDBSecurityGroupIngressRequest authorise = new AuthorizeDBSecurityGroupIngressRequest();
	   authorise.setDBSecurityGroupName("default");
	   authorise.setCIDRIP("82.43.211.250/32");
	   DBSecurityGroup group = rds.authorizeDBSecurityGroupIngress(authorise);

       for(IPRange t : group.getIPRanges()) {
          	System.out.println( "CIDRIP =  " + t.getCIDRIP() + " Status = " + t.getStatus());
       }
	   */
	   
        for(DBInstance i : result.getDBInstances()) {
        	
        	String createDate = (i.getInstanceCreateTime() == null) ? " " : DATE_FORMAT.format(i.getInstanceCreateTime());

                	System.out.println( createDate + " " +
     			                       i.getAvailabilityZone() + " " +
                			           i.getDBInstanceStatus() + " " +
     			                       i.getEngine() + " " + 
                			           i.getEngineVersion() + " " +
                			           i.getDBInstanceIdentifier() + " " +  
                			           i.getMasterUsername()+ " " +  
                			           i.getAllocatedStorage() + " " +  
                			           i.getEndpoint().getAddress()+ " " +  
                			           i.getEndpoint().getPort() );
                	
                    for(DBParameterGroupStatus t : i.getDBParameterGroups()) {
                    	System.out.println( "DBParameterGroupName =  " + t.getDBParameterGroupName()  );                    	
                    }
                    
                    for(DBSecurityGroupMembership t : i.getDBSecurityGroups()) {
                    	System.out.println( "DBSecurityGroupName =  " + t.getDBSecurityGroupName() + " Status = " + t.getStatus() );
                    	
                    }
        }
        
        /*
        RestoreDBInstanceFromDBSnapshotRequest request = new RestoreDBInstanceFromDBSnapshotRequest();
        request.setAvailabilityZone("eu-west-1b");
        request.setDBInstanceIdentifier("enttstdb1");
        request.setDBSnapshotIdentifier("test");
        DBInstance x = rds.restoreDBInstanceFromDBSnapshot(request);
        System.out.println("restored instance" + x.getDBInstanceIdentifier());
        */
	}

}