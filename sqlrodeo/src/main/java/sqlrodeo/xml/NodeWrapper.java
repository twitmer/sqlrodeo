package sqlrodeo.xml;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.UserDataHandler;

import sqlrodeo.SqlRodeoException;

public class NodeWrapper implements Node {

	private final Node delegate;

	public NodeWrapper(Node node) {
		this.delegate = node;
	}

	public Node appendChild(Node newChild) throws DOMException {
		return delegate.appendChild(newChild);
	}

	public Node cloneNode(boolean deep) {
		return delegate.cloneNode(deep);
	}

	public short compareDocumentPosition(Node other) throws DOMException {
		return delegate.compareDocumentPosition(other);
	}

	public String getAttribute(String attributeName) {
		if (getAttributes() == null) {
			return null;
		}

		Node attrNode = getAttributes().getNamedItem(attributeName);

		if (attrNode != null) {
			return attrNode.getTextContent();
		}

		return null;
	}

	public NamedNodeMap getAttributes() {
		return delegate.getAttributes();
	}

	public Map<String, String> getAttributesAsMap() {

		Map<String, String> result = new HashMap<>();

		NamedNodeMap attributes = getAttributes();
		if (null == attributes) {
			return result;
		}

		for (int i = 0; i < attributes.getLength(); ++i) {
			Node attrNode = attributes.item(i);
			result.put(attrNode.getNodeName(), attrNode.getNodeValue());
		}
		return result;
	}

	public String getBaseURI() {
		return delegate.getBaseURI();
	}

	// === Delegate methods only below this line.

	public NodeList getChildNodes() {
		return delegate.getChildNodes();
	}

	public List<Node> getChildNodesAsList() {
		NodeList nodelist = getChildNodes();

		List<Node> list = new ArrayList<Node>(nodelist.getLength());
		for (int i = 0; i < nodelist.getLength(); ++i) {
			list.add(nodelist.item(i));
		}
		return list;
	}

	public Object getFeature(String feature, String version) {
		return delegate.getFeature(feature, version);
	}

	public Node getFirstChild() {
		return delegate.getFirstChild();
	}

	public Node getLastChild() {
		return delegate.getLastChild();
	}

	public String getLocalName() {
		return delegate.getLocalName();
	}

	public String getNamespaceURI() {
		return delegate.getNamespaceURI();
	}

	public Node getNextSibling() {
		return delegate.getNextSibling();
	}

	public String getNodeName() {
		return delegate.getNodeName();
	}

	public short getNodeType() {
		return delegate.getNodeType();
	}

	public String getNodeValue() throws DOMException {
		return delegate.getNodeValue();
	}

	public Document getOwnerDocument() {
		return delegate.getOwnerDocument();
	}

	public Node getParentNode() {
		return delegate.getParentNode();
	}

	public String getPrefix() {
		return delegate.getPrefix();
	}

	public Node getPreviousSibling() {
		return delegate.getPreviousSibling();
	}

	public String getTextContent() throws DOMException {
		return delegate.getTextContent();
	}

	public Object getUserData(String key) {
		return delegate.getUserData(key);
	}

	public boolean hasAttributes() {
		return delegate.hasAttributes();
	}

	public boolean hasChildNodes() {
		return delegate.hasChildNodes();
	}

	public Node insertBefore(Node newChild, Node refChild) throws DOMException {
		return delegate.insertBefore(newChild, refChild);
	}

	public boolean isDefaultNamespace(String namespaceURI) {
		return delegate.isDefaultNamespace(namespaceURI);
	}

	public boolean isEqualNode(Node arg) {
		return delegate.isEqualNode(arg);
	}

	public boolean isSameNode(Node other) {
		return delegate.isSameNode(other);
	}

	public boolean isSupported(String feature, String version) {
		return delegate.isSupported(feature, version);
	}

	public String lookupNamespaceURI(String prefix) {
		return delegate.lookupNamespaceURI(prefix);
	}

	public String lookupPrefix(String namespaceURI) {
		return delegate.lookupPrefix(namespaceURI);
	}

	public void normalize() {
		delegate.normalize();
	}

	public Node removeChild(Node oldChild) throws DOMException {
		return delegate.removeChild(oldChild);
	}

	public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
		return delegate.replaceChild(newChild, oldChild);
	}

	public long resolveLineNumber() {
		return (Long) getUserData("lineNumber");
	}

	public URL resolveResourceUrl() {
		try {
			return new URL(getOwnerDocument().getDocumentURI());
		} catch (MalformedURLException e) {
			// We shouldn't ever get here, since we had to have read a URL to an
			// XML document to even create this node.
			throw new SqlRodeoException(e);
		}
	}

	public void setAttribute(String name, String value) {
		Document doc = getOwnerDocument();
		Attr attr = doc.createAttribute(name);
		attr.setNodeValue(value);
		getAttributes().setNamedItem(attr);
	}

	public void setNodeValue(String nodeValue) throws DOMException {
		delegate.setNodeValue(nodeValue);
	}

	public void setPrefix(String prefix) throws DOMException {
		delegate.setPrefix(prefix);
	}

	public void setTextContent(String textContent) throws DOMException {
		delegate.setTextContent(textContent);
	}

	public Object setUserData(String key, Object data, UserDataHandler handler) {
		return delegate.setUserData(key, data, handler);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(getNodeName());
		Map<String, String> attrs = getAttributesAsMap();
		if (attrs.size() > 0) {
			sb.append(", attrs=" + attrs);
		}
		Object resourceUrl = getUserData("resourceUrl");
		if (resourceUrl != null) {
			sb.append(", resourceUrl=" + resourceUrl);
		}

		if (delegate instanceof Text) {
			sb.append(", text=" + getNodeValue().trim());
		}
		return sb.toString();
	}
}
