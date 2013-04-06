package com.amazonaws.services.route53.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class XMLUtil {

	/** The Constant log. */
	private static final Logger log = Logger.getLogger(XMLUtil.class);

	public static Node getNode(Source source) {
		Node node;
		try {
			DOMResult dom = new DOMResult();
			Transformer trans = TransformerFactory.newInstance()
					.newTransformer();
			trans.transform(source, dom);
			node = dom.getNode();
		} catch (TransformerFactoryConfigurationError e) {
			log.error(e);
			throw new RuntimeException(e);
		} catch (TransformerException e) {
			log.error(e);
			throw new RuntimeException(e);
		}
		return node;
	}

	public static Node getNode(String source) {
		ByteArrayInputStream bs = new ByteArrayInputStream(source.getBytes());
		return getNode(bs);
	}
	
	public static DOMSource getSource(InputStream is) {
		Node node = getNode(is);
		return new DOMSource(node);
	}
	
	public static DOMSource getSource(Node node) {
		return new DOMSource(node);
	}
	
	public static DOMSource getSource(Resource resource) {
		Node node;
		try {
			node = getNode(resource.getInputStream());
		} catch (IOException e) {
			log.error(e);
			throw new RuntimeException(e);
		}
		return new DOMSource(node);
	}
	
	public static DOMSource getSource(String source) {
		Node node = getNode(source);
		return new DOMSource(node);
	}
	
	public static DOMSource getSource(Source source) {
		Node node = getNode(source);
		return new DOMSource(node);
	}	
	
	public static Node getNode(InputStream is) {
		Element node;
		try {
			node = DocumentBuilderFactory
			        .newInstance()
			        .newDocumentBuilder()
					.parse(is).getDocumentElement();
		} catch (SAXException e) {
			log.error(e);
			throw new RuntimeException(e);
		} catch (IOException e) {
			log.error(e);
			throw new RuntimeException(e);
		} catch (ParserConfigurationException e) {
			log.error(e);
			throw new RuntimeException(e);
		}
		return node;
	}	

	// used for debugging only
	public static String format(Node node) {
		return format(new DOMSource(node));
	}
	
	// used for debugging only
	public static String format(Source s) {
		try {
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			Result output = new StreamResult(bos);
			transformer.transform(s, output);
			return bos.toString().trim();
		} catch (Exception e) {
			log.error(e);
			throw new RuntimeException(e);
		}
	}
}
