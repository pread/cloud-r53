package com.amazonaws.services.route53.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.xml.xpath.Jaxp13XPathTemplate;
import org.springframework.xml.xpath.XPathExpressionFactoryBean;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.http.DefaultHttpClientFactory;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.rds.AmazonRDSClient;
import com.amazonaws.services.route53.Route53ServiceImpl;
import com.amazonaws.services.route53.rest.Route53ClientImpl;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.sns.AmazonSNSClient;

/**
 * The Class AppConfig.
 */
@Configuration
@ImportResource("classpath*:META-INF/spring/applicationContext.xml")
@Import({Route53ServiceImpl.class, Route53ClientImpl.class})
public class AppConfig {

	/** The key. */
	@Value("${access.key}") private String key;
	
	/** The secret. */
	@Value("${secret.key}") private String secret;
	
	/** The ec2 endpoint. */
	@Value("${ec2.endpoint}") private String ec2Endpoint;
	
	/** The sns endpoint. */
	@Value("${sns.endpoint}") private String snsEndpoint;
	
	/** The sdb endpoint. */
	@Value("${sdb.endpoint}") private String sdbEndpoint;
	
	/** The s3 endpoint. */
	@Value("${s3.endpoint}") private String s3Endpoint;
	
	/** The rds endpoint. */
	@Value("${rds.endpoint}") private String rdsEndpoint;
	
	/** The r53namespace. */
	@Value("${r53.namespace}") private String r53namespace;	
	
	/** The proxy port. */
	@Value("${r53.proxyPort}") private String proxyPort;
	
	/** The proxy host. */
	@Value("${r53.proxyHost}") private String proxyHost;
	
	/** The proxy username. */
	@Value("${r53.proxyUsername}") private String proxyUsername;
	
	/** The proxy password. */
	@Value("${r53.proxyPassword}") private String proxyPassword;
	
	/** The proxy domain. */
	@Value("${r53.proxyDomain}") private String proxyDomain;
	
	/** The proxy workstation. */
	@Value("${r53.proxyWorkstation}") private String proxyWorkstation;

    /**
     * Ec2 client.
     *
     * @return the amazon e c2
     */
    public @Bean AmazonEC2 ec2Client() {
    	AmazonEC2Client ec2 = new AmazonEC2Client(awsCredentials(), clientConfiguration());
    	if (StringUtils.isNotBlank(ec2Endpoint)) {
    		ec2.setEndpoint(ec2Endpoint);
    	}
        return ec2;
    }
    
    /**
     * Sns client.
     *
     * @return the amazon sns client
     */
    public @Bean AmazonSNSClient snsClient() {
    	AmazonSNSClient sns = new AmazonSNSClient(awsCredentials(), clientConfiguration());
    	if (StringUtils.isNotBlank(snsEndpoint)) {
            sns.setEndpoint(snsEndpoint);
    	}
        return sns;
    }
    
    /**
     * Sdb client.
     *
     * @return the amazon simple db client
     */
    public @Bean AmazonSimpleDBClient sdbClient() {
    	AmazonSimpleDBClient sdb = new AmazonSimpleDBClient(awsCredentials(), clientConfiguration());
    	if (StringUtils.isNotBlank(sdbEndpoint)) {
        	sdb.setEndpoint(sdbEndpoint);
    	}
        return sdb;
    }
    
    /**
     * S3 client.
     *
     * @return the amazon s3 client
     */
    public @Bean AmazonS3Client s3Client() {
    	AmazonS3Client s3 = new AmazonS3Client(awsCredentials(), clientConfiguration());
    	if (StringUtils.isNotBlank(s3Endpoint)) {
        	s3.setEndpoint(s3Endpoint);
    	}
        return s3;
    }

    /**
     * Rds client.
     *
     * @return the amazon rds client
     */
    public @Bean AmazonRDSClient rdsClient() {
    	AmazonRDSClient rds = new AmazonRDSClient(awsCredentials(), clientConfiguration());
    	if (StringUtils.isNotBlank(rdsEndpoint)) {
    		rds.setEndpoint(rdsEndpoint);
    	}
        return rds;
    }

	/**
	 * Rest client.
	 *
	 * @return the rest template
	 */
	@SuppressWarnings("rawtypes")
    public @Bean RestTemplate restClient() {
    	List<HttpMessageConverter<?>> messageConverters;
    	messageConverters = new ArrayList<HttpMessageConverter<?>>();	
		SourceHttpMessageConverter httpMessageConverter = new SourceHttpMessageConverter();
    	messageConverters.add(httpMessageConverter);
    	RestTemplate restClient = new RestTemplate();
    	restClient.setMessageConverters(messageConverters);
    	restClient.setRequestFactory(httpClientFactory());
        return restClient;
    }
	
    /**
     * Xpath template.
     *
     * @return the jaxp13 x path template
     */
    public @Bean Jaxp13XPathTemplate xpathTemplate() {
    	Jaxp13XPathTemplate xpathtemplate = new Jaxp13XPathTemplate();
    	Properties properties = new Properties();
    	properties.setProperty("tns", r53namespace);
    	xpathtemplate.setNamespaces(properties);
        return xpathtemplate;
    }
    
    /**
     * Hosted zone x path.
     *
     * @return the x path expression factory bean
     */
    public @Bean XPathExpressionFactoryBean hostedZoneXPath() {
    	XPathExpressionFactoryBean hostedZoneXPath = new XPathExpressionFactoryBean();
    	Properties properties = new Properties();
    	properties.setProperty("tns", r53namespace);
    	hostedZoneXPath.setNamespaces(properties);
    	hostedZoneXPath.setExpression("tns:ListHostedZonesResponse/tns:HostedZones/tns:HostedZone");
        return hostedZoneXPath;
    }
    
    /**
     * Name server x path.
     *
     * @return the x path expression factory bean
     */
    public @Bean XPathExpressionFactoryBean nameServerXPath() {
    	XPathExpressionFactoryBean nameServerXPath = new XPathExpressionFactoryBean();
    	Properties properties = new Properties();
    	properties.setProperty("tns", r53namespace);
    	nameServerXPath.setNamespaces(properties);
    	nameServerXPath.setExpression("tns:GetHostedZoneResponse/tns:DelegationSet/tns:NameServers/tns:NameServer");
        return nameServerXPath;
    }
    
    /**
     * Resource record set x path.
     *
     * @return the x path expression factory bean
     */
    public @Bean XPathExpressionFactoryBean resourceRecordSetXPath() {
    	XPathExpressionFactoryBean resourceRecordSetXPath = new XPathExpressionFactoryBean();
    	Properties properties = new Properties();
    	properties.setProperty("tns", r53namespace);
    	resourceRecordSetXPath.setNamespaces(properties);
    	resourceRecordSetXPath.setExpression("tns:ListResourceRecordSetsResponse/tns:ResourceRecordSets/tns:ResourceRecordSet");
        return resourceRecordSetXPath;
    }
    
    /**
     * Resource record x path.
     *
     * @return the x path expression factory bean
     */
    public @Bean XPathExpressionFactoryBean resourceRecordXPath() {
    	XPathExpressionFactoryBean resourceRecordXPath = new XPathExpressionFactoryBean();
    	Properties properties = new Properties();
    	properties.setProperty("tns", r53namespace);
    	resourceRecordXPath.setNamespaces(properties);
    	resourceRecordXPath.setExpression("tns:ListResourceRecordSetsResponse/tns:ResourceRecordSets/tns:ResourceRecordSet/tns:ResourceRecords/tns:ResourceRecord");
        return resourceRecordXPath;
    }

    /**
     * Creates the hosted zone x path.
     *
     * @return the x path expression factory bean
     */
    public @Bean XPathExpressionFactoryBean createHostedZoneXPath() {
    	XPathExpressionFactoryBean createHostedZoneXPath = new XPathExpressionFactoryBean();
    	Properties properties = new Properties();
    	properties.setProperty("tns", r53namespace);
    	createHostedZoneXPath.setNamespaces(properties);
    	createHostedZoneXPath.setExpression("tns:CreateHostedZoneResponse/tns:DelegationSet/tns:NameServers/tns:NameServer");
        return createHostedZoneXPath;
    }

    /**
     * Aws credentials.
     *
     * @return the basic aws credentials
     */
    private BasicAWSCredentials awsCredentials() {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(secret)) {
            return new BasicAWSCredentials(System.getenv("AWS_ACCESS_KEY_ID"), System.getenv("AWS_SECRET_ACCESS_KEY"));
        }
        return new BasicAWSCredentials(key, secret);
    }

    /**
     * Client configuration.
     *
     * @return the client configuration
     */
    private ClientConfiguration clientConfiguration() {

        int proxyPortInt = StringUtils.isEmpty(proxyPort) ? 0 : Integer.valueOf(proxyPort);
        return  (new ClientConfiguration())
                .withProxyHost(proxyHost)
                .withProxyPort(proxyPortInt)
                .withProxyUsername(proxyUsername)
                .withProxyPassword(proxyPassword)
                .withProxyWorkstation(proxyWorkstation)
                .withProxyDomain(proxyDomain);
    }

    /**
     * Http client factory.
     *
     * @return the http components client http request factory
     */
    private HttpComponentsClientHttpRequestFactory httpClientFactory() {
        HttpComponentsClientHttpRequestFactory httpClientFactory = new HttpComponentsClientHttpRequestFactory();
        DefaultHttpClientFactory clientFactory = new DefaultHttpClientFactory();
        httpClientFactory.setHttpClient(clientFactory.createHttpClient(clientConfiguration()));
        return httpClientFactory;
    }

}
