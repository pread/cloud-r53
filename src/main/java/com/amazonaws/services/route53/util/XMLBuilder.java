package com.amazonaws.services.route53.util;

import org.jdom.Comment;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.output.DOMOutputter;
import org.jdom.output.XMLOutputter;

import com.amazonaws.services.route53.model.Change;
import com.amazonaws.services.route53.model.ChangeResourceRecordSetsRequest;
import com.amazonaws.services.route53.model.CreateHostedZoneRequest;
import com.amazonaws.services.route53.model.ResourceRecord;

public class XMLBuilder {
	
	private static final Namespace XSI_NAMESPACE = Namespace.getNamespace("https://route53.amazonaws.com/doc/2010-10-01/");
	
	/**
	 * This method creates a JDOM document with elements that represent the
	 * CreateHostedZoneRequest. 
	 * 
	 * @return a JDOM Document that represents the properties of a CreateHostedZoneRequest.
	 */
	public static Document createDocument(CreateHostedZoneRequest request) {
		
		// Create the root element
		Element zoneElement = new Element("CreateHostedZoneRequest");
		zoneElement.setNamespace(XSI_NAMESPACE);

		// create the document
		Document myDocument = new Document(zoneElement);

		// add a comment
		zoneElement.addContent(new Comment("Request to Create a Hosted Zone"));
		
		// add some more elements
		zoneElement.addContent(new Element("Name", XSI_NAMESPACE).addContent(request.getName()));
		zoneElement.addContent(new Element("CallerReference", XSI_NAMESPACE).addContent(request.getCallerReference()));
		zoneElement.addContent(new Element("HostedZoneConfig", XSI_NAMESPACE).addContent(new Element("Comment", XSI_NAMESPACE).addContent(request.getConfig().getComment())));
		
		return myDocument;
	}
	
	/**
	 * This method creates a JDOM document with elements that represent the
	 * ChangeResourceRecordSetsRequest. 
	 * 
	 * @return a JDOM Document that represents the properties of a ChangeResourceRecordSetsRequest.
	 */
	public static Document createDocument(ChangeResourceRecordSetsRequest request) {
		
		// Create the root element
		Element zoneElement = new Element("ChangeResourceRecordSetsRequest");
		zoneElement.setNamespace(XSI_NAMESPACE);

		// create the document
		Document myDocument = new Document(zoneElement);

		// add a comment
		zoneElement.addContent(new Comment("Request to create or change your authoritative DNS information"));

		Element changesElement = new Element("Changes", XSI_NAMESPACE);
		
		/**/
		for(Change change : request.getChangeBatch().getChanges()) {

			Element changeElement = new Element("Change", XSI_NAMESPACE);
			changeElement.addContent(new Element("Action", XSI_NAMESPACE).addContent(change.getAction()));
			
			Element resourceRecordSetElement = new Element("ResourceRecordSet", XSI_NAMESPACE);
			resourceRecordSetElement.addContent(new Element("Name", XSI_NAMESPACE).addContent(change.getResourceRecordSet().getName()));
			resourceRecordSetElement.addContent(new Element("Type", XSI_NAMESPACE).addContent(change.getResourceRecordSet().getType()));
			resourceRecordSetElement.addContent(new Element("TTL", XSI_NAMESPACE).addContent(change.getResourceRecordSet().getTtl()));

			Element resourceRecordsElement = new Element("ResourceRecords", XSI_NAMESPACE);
			for(ResourceRecord resourceRecord : change.getResourceRecordSet().getResourceRecords()) {
				Element resourceRecordElement = new Element("ResourceRecord", XSI_NAMESPACE);
				resourceRecordElement.addContent(new Element("Value", XSI_NAMESPACE).addContent(resourceRecord.getValue()));
				resourceRecordsElement.addContent(resourceRecordElement);
			}
			
			resourceRecordSetElement.addContent(resourceRecordsElement);	
			changeElement.addContent(resourceRecordSetElement);
			changesElement.addContent(changeElement);
		}

		Element changeBatchElement = new Element("ChangeBatch", XSI_NAMESPACE);
		changeBatchElement.addContent(new Element("Comment", XSI_NAMESPACE).addContent(request.getChangeBatch().getComment()));
		changeBatchElement.addContent(changesElement);
		
		zoneElement.addContent(changeBatchElement);
			
		// add some more elements
		return myDocument;
	}

	public static org.w3c.dom.Document convertToDOM(org.jdom.Document jdomDoc) {
		DOMOutputter outputter = new DOMOutputter();
		try {
			return outputter.output(jdomDoc);
		} catch (JDOMException e) {
			throw new RuntimeException(e);
		}
	}	

	public static void format(Document doc) {
	    XMLOutputter outputter = new XMLOutputter();
	    outputter.outputString(doc);
	    //outputter.output(doc, System.out);
	}	

}
