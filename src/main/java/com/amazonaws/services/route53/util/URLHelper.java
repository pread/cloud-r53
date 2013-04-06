package com.amazonaws.services.route53.util;

import org.springframework.http.MediaType;

public class URLHelper {

	private static final String XML  = ".xml";
	private static final String JSON = ".json";
	
	/**
	 * Gets the URL.
	 *
	 * @return the URL
	 */
	public static final String getURL(String endpoint,
			                          String applicationNode,
			                          String resource) {		
		StringBuffer strbuf = new StringBuffer();
		strbuf.append(endpoint);
		strbuf.append("/");
		strbuf.append(applicationNode);
		strbuf.append("/");
		strbuf.append(resource);
		return strbuf.toString();
	}
	
	/**
	 * Gets the URL.
	 *
	 * @return the URL
	 */
	public static final String getURLwithArgs(String endpoint,
                                              String applicationNode,
                                              String resource, 
                                              MediaType mediaType) {						
		StringBuffer strbuf = new StringBuffer();
		strbuf.append(endpoint);
		strbuf.append("/");
		strbuf.append(applicationNode);
		strbuf.append("/");
		strbuf.append(resource);
		strbuf.append("/result");
		if (mediaType.includes(MediaType.APPLICATION_XML)) {
			strbuf.append(XML);
		} else if (mediaType.includes(MediaType.APPLICATION_JSON)) {
			strbuf.append(JSON);
		}
		return strbuf.toString();
	}
	
	/**
	 * Gets the URL.
	 *
	 * @return the URL
	 */
	public static final String getURL(String endpoint,
			                          String applicationNode,
			                          String resource, 
			                          MediaType mediaType) {	
				
		StringBuffer strbuf = new StringBuffer();
		strbuf.append(endpoint);
		strbuf.append("/");
		strbuf.append(applicationNode);
		strbuf.append("/");
		strbuf.append(resource);
		if (mediaType.includes(MediaType.APPLICATION_XML)) {
			strbuf.append(XML);
		} else if (mediaType.includes(MediaType.APPLICATION_JSON)) {
			strbuf.append(JSON);
		}
		return strbuf.toString();
	}
	
}
