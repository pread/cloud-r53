package com.amazonaws.services.route53.encoding;

import java.security.SignatureException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.log4j.Logger;

/**
 * The Class Encoding.
 */
public class Encoding {

    /** The Constant log. */
    private static final Logger log = Logger.getLogger(Encoding.class);
	
	/** The Constant HMAC_SHA1_ALGORITHM. */
	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
	
	/** The Constant UTF8. */
	private static final String UTF8 = "UTF-8";

	/**
	 * Performs base64-encoding of input bytes.
	 * 
	 * @param rawData
	 *            * Array of bytes to be encoded.
	 * @return * The base64 encoded string representation of rawData.
	 */
	public static String encodeBase64(byte[] rawData) {
		return Base64.encodeBytes(rawData);
	}

	/**
	 * Generate signature.
	 *
	 * @param data the data
	 * @param key the key
	 * @return the string
	 */
	public static String generateSignature(String data, String key) {
		try {
			String signature = calculateRFC2104HMAC(data, key);
			log.info("Signature: " + signature);
			return signature;

		} catch (SignatureException e) {
			log.error(e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Computes RFC 2104-compliant HMAC signature.
	 *
	 * @param data The data to be signed.
	 * @param key - The signing key.
	 * @return The Base64-encoded RFC 2104-compliant HMAC signature.
	 * @throws SignatureException the signature exception
	 */
	public static String calculateRFC2104HMAC(String data, String key)
			throws SignatureException {

		final String result;
		try {

			// get an hmac_sha1 key from the raw key bytes
			SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(UTF8), HMAC_SHA1_ALGORITHM);

			// get an hmac_sha1 Mac instance and initialise with the signing key
			Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
			mac.init(signingKey);

			// compute the hmac on input data bytes
			byte[] rawHmac = mac.doFinal(data.getBytes(UTF8));

			// base64-encode the hmac
			result = Encoding.encodeBase64(rawHmac);

		} catch (Exception e) {
			log.error(e);
			throw new SignatureException("Failed to generate HMAC : " + e.getMessage());
		}
		return result;
	}

}
