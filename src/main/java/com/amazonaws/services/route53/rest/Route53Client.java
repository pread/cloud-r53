package com.amazonaws.services.route53.rest;

import java.util.Map;

import org.springframework.http.HttpMethod;

/**
 * The RESTful interface.
 */
public interface Route53Client {

	/**
	 * Request.
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
	<T> T request(String uri, HttpMethod method, Map<String, String> vars, Class<T> responseType);
	/**
	 * Request.
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
	<T> T request(String uri, HttpMethod method, Object body, Map<String, String> vars, Class<T> responseType);

	/**
	 * Gets the AWS server date.
	 * 
	 * @return the server date as String.
	 */
	String getServerDate();

}
