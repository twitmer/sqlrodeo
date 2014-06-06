package sqlrodeo.xml;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public final class Parser {

    private static final Logger log = LoggerFactory.getLogger(Parser.class);

    private static final URL schemaURL = Parser.class
	    .getResource("/SqlRodeoSchema.xsd");

    public static void display(String indent, Node node) {
	log.info(indent + "Node: " + new NodeWrapper(node).toString());
	for (int i = 0; i < node.getChildNodes().getLength(); i++) {
	    Node childNode = node.getChildNodes().item(i);
	    display(indent + "  ", childNode);
	}
    }

    /**
     * Oh this annoys me: https://www.java.net/node/667186f
     * 
     * @param e
     */
    public static void removeWhitespaceNodes(Element e) {

	NodeList children = e.getChildNodes();

	// Go through the list backwards so we don't affect the element indices
	// as we cull children.

	for (int i = children.getLength() - 1; i >= 0; i--) {
	    Node child = children.item(i);
	    if (child instanceof Text
		    && ((Text) child).getData().trim().length() == 0) {
		// log.debug("Removed whitespace node");
		e.removeChild(child);
	    } else if (child instanceof Element) {
		removeWhitespaceNodes((Element) child);
	    }
	}
    }

    public Node parse(URL resourceURL) throws SAXException, IOException,
	    ParserConfigurationException, DOMException, URISyntaxException {

	if (log.isDebugEnabled()) {
	    log.debug(String.format(
		    "parse: resourceURL".replaceAll(", ", "=%s, ") + "=%s",
		    resourceURL));
	}

	// Step: Parse the document
	log.info("Parsing resourceURL:" + resourceURL.toExternalForm());
	final InputSource inputSource = new InputSource(
		resourceURL.toExternalForm());
	Document document = PositionalXmlReader.readXML(resourceURL,
		inputSource);

	// TODO: Should I be passing Document instead of a root Element around?
	Element docRoot = document.getDocumentElement();

	// Step: Remove useless whitespace nodes
	log.info("Culling whitespace #text nodes from:"
		+ resourceURL.toExternalForm());
	removeWhitespaceNodes(docRoot);

	log.info("Displaying Node tree for "
		+ docRoot.getOwnerDocument().getDocumentURI());
	display("", docRoot);

	return docRoot;
    }

    public Node parseAndValidate(URL resourceURL) throws SAXException,
	    IOException, ParserConfigurationException, DOMException,
	    URISyntaxException {

	if (log.isDebugEnabled()) {
	    log.debug(String.format(
		    "parseAndValidate: resourceURL".replaceAll(", ", "=%s, ")
			    + "=%s", resourceURL));
	}

	// Step: Validate the XML
	validateAgainstSchema(resourceURL);

	// Step: Parse the document
	return parse(resourceURL);
    }

    public void validateAgainstSchema(URL resourceURL) throws SAXException,
	    IOException {

	if (log.isDebugEnabled()) {
	    log.debug(String.format(
		    "validateAgainstSchema: resourceURL".replaceAll(", ",
			    "=%s, ") + "=%s", resourceURL));
	}

	SchemaFactory sf = SchemaFactory
		.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	Schema schema = sf.newSchema(schemaURL);
	Validator validator = schema.newValidator();
	log.debug("Validating " + resourceURL);
	validator.validate(new StreamSource(resourceURL.getFile()));
	log.debug("Resource passed schema validation: "
		+ resourceURL.toExternalForm());
    }

}
