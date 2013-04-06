package com.amazonaws.services.route53.encoding;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * 
 * @author Phillip Read
 */
public class EncodingTest {
	
	/**
	 * Test Case for generating a security signature.
	 * RFC 2104-compliant HmacSHA1 or HmacSHA256 hash.
	 */
	@Test
	public void generateSignatureRFC2104HMAC() {

		String serverDate = "Thu, 14 Aug 2008 17:08:48 GMT";
		String secret = "/Ml61L9VxlzloZ091/lkqVV5X1/YvaJtI9hW4Wr9";
		String signature = Encoding.generateSignature(serverDate, secret);
		
		assertEquals("4cP0hCJsdCxTJ1jPXo7+e/YSu0g=", signature);
		System.out.println("Signature: " + signature);
	}

}