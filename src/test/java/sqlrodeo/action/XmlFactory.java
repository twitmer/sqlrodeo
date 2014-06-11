package sqlrodeo.action;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlFactory {

    private static final Logger log = LoggerFactory.getLogger(XmlFactory.class);

    private XmlFactory() {
    }

    public static Document build(String content) throws SAXException,
	    IOException, ParserConfigurationException {

	if (log.isDebugEnabled()) {
	    log.debug(String.format("build: content".replaceAll(", ", "=%s, ")
		    + "=%s", content));
	}

	String xmlString = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
		+ content;

	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	DocumentBuilder builder;
	builder = factory.newDocumentBuilder();
	Document document = builder.parse(new InputSource(new StringReader(
		xmlString)));

	// Set Document URI to point to the Class that invoked this method.
	StackTraceElement[] stes = Thread.currentThread().getStackTrace();

	String path = "/" + stes[2].getClassName().replace(".", "/") + ".class";
	log.debug("path=" + path);
	URL resourceUrl = XmlFactory.class.getResource(path);
	log.debug("resourceUrl1 = " + resourceUrl);

	document.setDocumentURI(resourceUrl.toExternalForm());
	log.info("Doc URI=" + document.getDocumentURI());

	// Put line number on all elements in document.
	NodeList list = document.getElementsByTagName("*");
	Long lineNumber = Long.valueOf(stes[2].getLineNumber());
	for (int i = 0; i < list.getLength(); i++) {
	    Element element = (Element) list.item(i);
	    element.setUserData("lineNumber", lineNumber, null);
	}

	return document;
    }
}
