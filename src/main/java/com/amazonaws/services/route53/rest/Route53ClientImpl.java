package com.amazonaws.services.route53.rest;

import com.amazonaws.services.route53.encoding.Encoding;
import com.amazonaws.services.route53.model.*;
import com.amazonaws.services.route53.util.XMLBuilder;
import com.amazonaws.services.route53.util.XMLUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import org.springframework.xml.xpath.NodeMapper;
import org.springframework.xml.xpath.XPathExpression;
import org.springframework.xml.xpath.XPathOperations;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.transform.Source;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * RESTful client implementation for AWS Route 53.
 */
@Component
public class Route53ClientImpl implements Route53Client {

    private static final Logger log = Logger.getLogger(Route53ClientImpl.class);
	   
	/** The Constant REQUEST_ID. */
	private static final String REQUEST_ID = "x-amzn-RequestId";

	/** The Constant SERVER_DATE. */
	private static final String SERVER_DATE = "date";

	/** Inject the RESTful Client. */
	@Inject
	@Named("restClient")
	private RestTemplate restTemplate;

	/** Inject the template for XPath Operations. */
	@Inject
	@Named("xpathTemplate")
	private XPathOperations xpathTemplate;
	
	/** Inject the template for XPath Expressions. */
	@Inject
	@Named("hostedZoneXPath")
	private XPathExpression hostedZoneXPath;

	/** Inject the template for XPath Expressions. */
	@Inject
	@Named("nameServerXPath")
	private XPathExpression nameServerXPath;
	
	/** Inject the template for XPath Expressions. */
	@Inject
	@Named("resourceRecordSetXPath")
	private XPathExpression resourceRecordSetXPath;

	/** Inject the template for XPath Expressions. */
	@Inject
	@Named("resourceRecordXPath")
	private XPathExpression resourceRecordXPath;

	/** Inject the template for XPath Expressions. */
	@Inject
	@Named("createHostedZoneXPath")
	private XPathExpression createHostedZoneXPath;
	
	/** The R53 Endpoint. */
	@Value("${r53.endpoint}")
	private String host;

	/** The key. */
	@Value("${r53.access.key}")
	private String key;

	/** The secret. */
	@Value("${r53.secret.key}")
	private String secret;
	
	
	
	private String getAccessKey() {
		if (StringUtils.isBlank(key)) {
			return System.getenv("AWS_ACCESS_KEY_ID");
		}
		return key;
	}
	
	private String getSecretAccessKey() {
		if (StringUtils.isBlank(secret)) {
			return System.getenv("AWS_SECRET_ACCESS_KEY");
		} 
		return secret;
	}
	
	
	public <T> T request(String uri, HttpMethod method, Map<String, String> vars, Class<T> responseType) {		
		return request(uri, method, null, vars, responseType);		
	}

	/**
	 * RESTful Client call to the AWS Route53 Service.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param uri
	 *            the uri
	 * @param method
	 *            the method
	 * @param responseType
	 *            the response type
	 * @return the list
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <T> T request(String uri, HttpMethod method, Object body, Map<String, String> vars, Class<T> responseType) {

		String serverDate = getServerDate();
		String signature = Encoding.generateSignature(serverDate, getSecretAccessKey());

		StringBuilder url = new StringBuilder();
		HttpHeaders headers = getHeaders(serverDate, signature);
		headers.setContentType(MediaType.APPLICATION_XML);
		
		HttpEntity entity;
		if (body == null) {
			entity = new HttpEntity<String>(headers);	
		} else {
			entity = new HttpEntity(structureRequestMapper(body), headers);			
		}

		Source response = restTemplate.exchange(url.append(host).append(uri).toString(), 
				                                method, 
				                                entity,
				                                Source.class, vars).getBody();
		
		T mappedPojo = structureResponseMapper(response, responseType);

		Assert.isTrue(response != null);
		
		return mappedPojo;
	}
	
	@SuppressWarnings("unchecked")
	private <T> T structureResponseMapper(Source result, Class<T> responseType) {

		if (responseType.isAssignableFrom(ListHostedZonesResponse.class)) {
			return (T) listHostedZonesResponseMapper(result);
		} else if (responseType.isAssignableFrom(HostedZoneResponse.class)) {
			return (T) hostedZoneResponseMapper(result);
		} else if (responseType.isAssignableFrom(ChangeResponse.class)) {
			return (T) changeResponseMapper(result);
		} else if (responseType.isAssignableFrom(ListResourceRecordSetsResponse.class)) {
			return (T) resourceRecordSetMapper(result);
		} else if (responseType.isAssignableFrom(CreateHostedZoneResponse.class)) {
			return (T) createHostedZoneSetMapper(result);
		} else if (responseType.isAssignableFrom(ChangeResourceRecordSetsResponse.class)) {
			return (T) changeResourceRecordSetsMapper(result);
		}
		return null;
	}
	
    private ChangeResourceRecordSetsResponse changeResourceRecordSetsMapper(Source response) {
		
		Node node = xpathTemplate.evaluateAsNode("tns:ChangeResourceRecordSetsResponse/tns:ChangeInfo", response);							
		Element elem = (Element) node;
		Element id = (Element) elem.getElementsByTagName("Id").item(0);
		Element status = (Element) elem.getElementsByTagName("Status").item(0);

		// DateString compatible with ISO8601 standard (UTC).
		Element dateTime = (Element) elem.getElementsByTagName("SubmittedAt").item(0);
		DateTime submittedAt = new DateTime(dateTime.getTextContent(), DateTimeZone.UTC);
		
		return new ChangeResourceRecordSetsResponse(ChangeInfo.with()
                        .id(id.getTextContent())
                        .status(status.getTextContent())
                        .submittedAt(submittedAt.toCalendar(Locale.UK))
                        .create());
    }
	
	private Source structureRequestMapper(Object request) {
		if (request.getClass().isAssignableFrom(CreateHostedZoneRequest.class)) {
			XMLBuilder.format(XMLBuilder.createDocument((CreateHostedZoneRequest) request));
			Document doc = XMLBuilder.convertToDOM(XMLBuilder.createDocument((CreateHostedZoneRequest) request));
			return XMLUtil.getSource(doc);
		} else if (request.getClass().isAssignableFrom(ChangeResourceRecordSetsRequest.class)) {
			XMLBuilder.format(XMLBuilder.createDocument((ChangeResourceRecordSetsRequest) request));
			Document doc = XMLBuilder.convertToDOM(XMLBuilder.createDocument((ChangeResourceRecordSetsRequest) request));			
			
			log.info("ChangeResourceRecordSetsRequest XML: \n\n" + XMLUtil.format(doc));
			
			ChangeResourceRecordSetsRequest output = (ChangeResourceRecordSetsRequest) request;
			System.out.println("\nUpdated ... "); 
			for(Change i : output.getChangeBatch().getChanges()) {
				System.out.println("Name  = " + i.getResourceRecordSet().getName()); 
				for(ResourceRecord j : i.getResourceRecordSet().getResourceRecords()) {
					System.out.println("Type  = " + i.getResourceRecordSet().getType()); 
					System.out.println("TTL  = " + i.getResourceRecordSet().getTtl()); 
					System.out.println("Value  = " + j.getValue()); 					
				}
			}			
			return XMLUtil.getSource(doc);
		}
		return null;
	}	

	private CreateHostedZoneResponse createHostedZoneSetMapper(Source response) {
		
		Node node = XMLUtil.getNode(response);		
		String id = xpathTemplate.evaluateAsString("tns:CreateHostedZoneResponse/tns:HostedZone/tns:Id", XMLUtil.getSource(node));
		String name = xpathTemplate.evaluateAsString("tns:CreateHostedZoneResponse/tns:HostedZone/tns:Name", XMLUtil.getSource(node));
		String callerRef = xpathTemplate.evaluateAsString("tns:CreateHostedZoneResponse/tns:HostedZone/tns:CallerReference", XMLUtil.getSource(node));
		String comment = xpathTemplate.evaluateAsString("tns:CreateHostedZoneResponse/tns:HostedZone/tns:Config/tns:Comment", XMLUtil.getSource(node));

		String changeId = xpathTemplate.evaluateAsString("tns:CreateHostedZoneResponse/tns:ChangeInfo/tns:Id", XMLUtil.getSource(node));
		String status = xpathTemplate.evaluateAsString("tns:CreateHostedZoneResponse/tns:ChangeInfo/tns:Status", XMLUtil.getSource(node));
		
		// DateString compatible with ISO8601 standard (UTC).
		String dateTime = xpathTemplate.evaluateAsString("tns:CreateHostedZoneResponse/tns:ChangeInfo/tns:SubmittedAt", XMLUtil.getSource(node));
		DateTime submittedAt = new DateTime(dateTime, DateTimeZone.UTC);
		
		ChangeInfo changeInfo = ChangeInfo.with()
	            .id(changeId)
	            .status(status)
	            .submittedAt(submittedAt.toCalendar(Locale.UK))
	            .create();

    	@SuppressWarnings("unchecked")
		List<NameServer> list = createHostedZoneXPath.evaluate(node,
				new NodeMapper() {
					public NameServer mapNode(Node node, int i) throws DOMException {							
						Element elem = (Element) node;
						//Element name = (Element) elem.getElementsByTagName("NameServer").item(0);						
						return new NameServer(elem.getTextContent());
					}
				});
    	
		return new CreateHostedZoneResponse(HostedZone.with()
                .id(id)
                .name(name)
                .callerRef(callerRef)
                .config(new Config(comment != null ? comment : null))
                .create(), changeInfo, list);
    }	
	
	private ListResourceRecordSetsResponse resourceRecordSetMapper(Source response) {
		
		Node node = xpathTemplate.evaluateAsNode("tns:ListResourceRecordSetsResponse", response);
		
    	@SuppressWarnings("unchecked")
		List<ResourceRecordSet> list = resourceRecordSetXPath.evaluate(node.getOwnerDocument(),
				new NodeMapper() {
					public ResourceRecordSet mapNode(Node node, int i) throws DOMException {							
						Element elem = (Element) node;
						Element name = (Element) elem.getElementsByTagName("Name").item(0);
						Element type = (Element) elem.getElementsByTagName("Type").item(0);
						Element ttl = (Element) elem.getElementsByTagName("TTL").item(0);
						List<ResourceRecord> list = resourceRecordXPath.evaluate(node.getOwnerDocument(),
								new NodeMapper() {
									public ResourceRecord mapNode(Node node, int i) throws DOMException {							
										Element elem = (Element) node;
										Element value = (Element) elem.getElementsByTagName("Value").item(0);
										return new ResourceRecord(value.getTextContent());
									}
								});						
						return ResourceRecordSet.with()
					            .name(name.getTextContent())
					            .type(type.getTextContent())
					            .ttl(ttl.getTextContent())
					            .resourceRecords(list)
					            .create();
					}
				});
    	
		Element eval = (Element) node;
		Element isTruncated = (Element) eval.getElementsByTagName("IsTruncated").item(0);
		Element nextRecordName = (Element) eval.getElementsByTagName("NextRecordName").item(0);
		Element nextRecordType = (Element) eval.getElementsByTagName("NextRecordType").item(0);
		Element maxItems = (Element) eval.getElementsByTagName("MaxItems").item(0);

		return ListResourceRecordSetsResponse.with()
	            .resourceRecordSet(list)
	            .maxItems(maxItems != null ? Integer.valueOf(maxItems.getTextContent()) : null)
	            .nextRecordName(nextRecordName != null ? nextRecordName.getTextContent() : null)
	            .nextRecordType(nextRecordType != null ? nextRecordType.getTextContent() : null)
	            .isTruncated(isTruncated != null ? Boolean.valueOf(isTruncated.getTextContent()) : null)
	            .create();
    }
	
	private ChangeResponse changeResponseMapper(Source response) {
		
		Node node = xpathTemplate.evaluateAsNode("tns:GetChangeResponse/tns:ChangeInfo", response);							
		Element elem = (Element) node;
		Element id = (Element) elem.getElementsByTagName("Id").item(0);
		Element status = (Element) elem.getElementsByTagName("Status").item(0);

		// DateString compatible with ISO8601 standard (UTC).
		Element dateTime = (Element) elem.getElementsByTagName("SubmittedAt").item(0);
		DateTime submittedAt = new DateTime(dateTime.getTextContent(), DateTimeZone.UTC);
		
		return new ChangeResponse(ChangeInfo.with()
                        .id(id.getTextContent())
                        .status(status.getTextContent())
                        .submittedAt(submittedAt.toCalendar(Locale.UK))
                        .create());
    }
	
	private HostedZoneResponse hostedZoneResponseMapper(Source response) {
		
		Node node = xpathTemplate.evaluateAsNode("tns:GetHostedZoneResponse/tns:HostedZone", response);							
		Element elem = (Element) node;
		Element id = (Element) elem.getElementsByTagName("Id").item(0);
		Element name = (Element) elem.getElementsByTagName("Name").item(0);
		Element callerReference = (Element) elem.getElementsByTagName("CallerReference").item(0);			
		Element comment = (Element) elem.getElementsByTagName("Comment").item(0);
		
    	@SuppressWarnings("unchecked")
		List<NameServer> list = nameServerXPath.evaluate(node.getOwnerDocument(),
				new NodeMapper() {
					public NameServer mapNode(Node node, int i) throws DOMException {
						return new NameServer(node.getTextContent());
					}
				});
    	
    	return HostedZoneResponse.with()
    	               .hostedZone(HostedZone.with()
    	                       .id(id.getTextContent())
    	                       .name(name.getTextContent())
    	                       .callerRef(callerReference.getTextContent())
    	                       .config(new Config(comment != null ? comment.getTextContent() : null))
    	                       .create())
    	               .nameServers(list)
    	               .create();
    }

	private ListHostedZonesResponse listHostedZonesResponseMapper(Source response) {

		Node node = xpathTemplate.evaluateAsNode("tns:ListHostedZonesResponse", response);
		
    	@SuppressWarnings("unchecked")
		List<HostedZone> list = hostedZoneXPath.evaluate(node.getOwnerDocument(),
				new NodeMapper() {
					public HostedZone mapNode(Node node, int i) throws DOMException {							
						Element elem = (Element) node;
						Element id = (Element) elem.getElementsByTagName("Id").item(0);
						Element name = (Element) elem.getElementsByTagName("Name").item(0);
						Element callerReference = (Element) elem.getElementsByTagName("CallerReference").item(0);			
						Element comment = (Element) elem.getElementsByTagName("Comment").item(0);
						
						return HostedZone.with()
		                   .id(id.getTextContent())
		                   .name(name.getTextContent())
		                   .callerRef(callerReference.getTextContent())
		                   .config(new Config(comment != null ? comment.getTextContent() : null))
		                   .create();
					}
				});
    	Element eval = (Element) node;
		Element marker = (Element) eval.getElementsByTagName("Marker").item(0);
		Element isTruncated = (Element) eval.getElementsByTagName("IsTruncated").item(0);
		Element maxItems = (Element) eval.getElementsByTagName("MaxItems").item(0);
		
    	return ListHostedZonesResponse.with()
    	                           .hostedZones(list)
    	                           .maxItems(maxItems != null ? Integer.valueOf(maxItems.getTextContent()) : null)
    	                           .isTruncated(isTruncated != null ? Boolean.valueOf(isTruncated.getTextContent()) : null)
    	                           .nextMarker(marker != null ? marker.getTextContent() : null)
    	                           .create();
    }
	
	/**
	 * Gets the headers.
	 * 
	 * @param serverDate
	 *            the server date
	 * @param signature
	 *            the signature
	 * @return the headers
	 */
	private HttpHeaders getHeaders(String serverDate, String signature) {	
		HttpHeaders headers = new HttpHeaders();
		String amznAuth = "AWS3-HTTPS AWSAccessKeyId=" + getAccessKey()
				+ ",Algorithm=HmacSHA1,Signature=" + signature;
		headers.set("Date", serverDate);
		headers.set("Accept", "text/xml; charset=UTF-8");
		headers.set("X-Amzn-Authorization", amznAuth);
		return headers;
	}

	/**
	 * Gets the server date.
	 * 
	 * @return the server date
	 */
	public String getServerDate() {

		StringBuilder url = new StringBuilder();
		RestTemplate basicTemplate = new RestTemplate();

		ResponseEntity<String> entity = basicTemplate.getForEntity(
				url.append(host).append("/date").toString(), String.class);

		HttpStatus statusCode = entity.getStatusCode();
		Assert.isTrue(HttpStatus.OK.equals(statusCode));

		HttpHeaders headers = entity.getHeaders();
		String requestId = headers.getFirst(REQUEST_ID);
		String serverDate = headers.getFirst(SERVER_DATE);
		log.info("RequestId: " + requestId + "\nServer Date: " + serverDate);
		return serverDate;
	}

}
