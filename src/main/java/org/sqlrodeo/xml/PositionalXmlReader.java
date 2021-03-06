package org.sqlrodeo.xml;

import java.io.IOException;
import java.net.URL;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlrodeo.Action;
import org.sqlrodeo.SqlRodeoException;
import org.sqlrodeo.implementation.ValidationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Stolen and adapted from:
 * http://stackoverflow.com/questions/4915422/get-line-number-from-xml-node-java
 */
class PositionalXmlReader {

	final static String LINE_NUMBER_KEY_NAME = "lineNumber";

	private static final Logger log = LoggerFactory
			.getLogger(PositionalXmlReader.class);

	public static Document readXML(final URL resourceURL, final InputSource is)
			throws IOException, SAXException {

		if (log.isDebugEnabled()) {
			log.debug("readXML()");
		}

		final Document doc;

		SAXParser parser;

		try {
			final SAXParserFactory factory = SAXParserFactory.newInstance();
			parser = factory.newSAXParser();
			final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
					.newInstance();
			final DocumentBuilder docBuilder = docBuilderFactory
					.newDocumentBuilder();
			doc = docBuilder.newDocument();
		} catch (final ParserConfigurationException e) {
			throw new SqlRodeoException(
					"Can't create SAX parser / DOM builder.", e);
		}

		final Stack<Element> elementStack = new Stack<Element>();

		final StringBuilder textBuffer = new StringBuilder();

		final DefaultHandler handler = new DefaultHandler() {

			private Locator locator = null;

			// Outputs text accumulated under the current node
			private void addTextIfNeeded() {
				if (textBuffer.length() > 0) {
					final Element el = elementStack.peek();
					final Node textNode = doc.createTextNode(textBuffer
							.toString());
					el.appendChild(textNode);
					textBuffer.delete(0, textBuffer.length());
				}
			}

			@Override
			public void characters(final char ch[], final int start,
					final int length) throws SAXException {
				textBuffer.append(ch, start, length);
			}

			@Override
			public void endElement(final String uri, final String localName,
					final String qName) {
				if (log.isDebugEnabled()) {
					log.debug(String.format(
							"endElement: uri, localName, qName".replaceAll(
									", ", "=%s, ") + "=%s", uri, localName,
							qName));
				}
				addTextIfNeeded();
				final Element closedEl = elementStack.pop();

				// Cull whitespace nodes from this element.
				removeWhitespaceNodes(closedEl);

				// Add element to parent, which could be the document if this is
				// the root element.
				if (elementStack.isEmpty()) { // Is this the root element?
					doc.appendChild(closedEl);
				} else {
					final Element parentEl = elementStack.peek();
					parentEl.appendChild(closedEl);
				}

				// Create and validate action that corresponds to this element.
				Action action = ActionFactory.build(closedEl);
				closedEl.setUserData("action", action, null);
				try {
					log.debug("Validating: " + action);
					action.validate();
				} catch (SqlRodeoException e) {
					throw e;
				} catch (Exception e) {
					throw new ValidationException(action, e);
				}
			}

			@Override
			public void setDocumentLocator(final Locator locator) {
				this.locator = locator; // Save the locator, so that it can be
				// used later for line tracking when
				// traversing nodes.
			}

			@Override
			public void startElement(final String uri, final String localName,
					final String qName, final Attributes attributes)
					throws SAXException {
				if (log.isDebugEnabled()) {
					log.debug(String.format(
							"startElement: uri, localName, qName, attributes"
									.replaceAll(", ", "=%s, ") + "=%s", uri,
							localName, qName, attributes));
				}
				addTextIfNeeded();
				final Element el = doc.createElement(qName);
				for (int i = 0; i < attributes.getLength(); i++) {
					el.setAttribute(attributes.getQName(i),
							attributes.getValue(i));
				}
				el.setUserData(LINE_NUMBER_KEY_NAME,
						Long.valueOf(locator.getLineNumber()), null);

				// Record the document URI.
				if (qName.equals("sql-rodeo")) {
					el.getOwnerDocument().setDocumentURI(
							resourceURL.toExternalForm());
				}

				elementStack.push(el);
			}
		};

		parser.parse(is, handler);
		return doc;
	}

	/**
	 * Oh this annoys me: https://www.java.net/node/667186f
	 * 
	 * @param e
	 */
	public static void removeWhitespaceNodes(Element e) {

		if (log.isDebugEnabled()) {
			log.debug("removeWhitespaceNodes " + e.getTagName());
		}

		NodeList children = e.getChildNodes();

		// Go through the list backwards so we don't affect the element indices
		// as we cull children.

		boolean removed = false;
		for (int i = children.getLength() - 1; i >= 0; i--) {
			Node child = children.item(i);
			if (child instanceof Text
					&& ((Text) child).getData().trim().length() == 0) {
				e.removeChild(child);
				removed = true;
				// log.debug("Removed whitespace node from " + e.getTagName()
				// + ": [" + child.getTextContent() + "]: ["
				// + e.getTextContent() + "]");
			} else if (child instanceof Element) {
				removeWhitespaceNodes((Element) child);
			}
		}
		if (removed) {
			log.debug("Removed whitespace node from " + e.getTagName() + ": ["
					+ e.getTextContent() + "]");
		}

	}

}
