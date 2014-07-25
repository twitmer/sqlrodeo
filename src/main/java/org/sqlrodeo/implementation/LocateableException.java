package org.sqlrodeo.implementation;

import java.net.URL;

import org.sqlrodeo.SqlRodeoException;
import org.w3c.dom.Node;

@SuppressWarnings("serial")
public abstract class LocateableException extends SqlRodeoException {

	private final long lineNumber;

	private final Node node;

	private final URL url;

	public LocateableException(URL url, long lineNumber, Node node,
			String message) {
		super(asString(message, url, lineNumber, node));
		this.url = url;
		this.lineNumber = lineNumber;
		this.node = node;
	}

	public LocateableException(URL url, long lineNumber, Node node,
			String message, Throwable throwable) {
		super(asString(message, url, lineNumber, node), throwable);
		this.url = url;
		this.lineNumber = lineNumber;
		this.node = node;
	}

	public LocateableException(URL url, long lineNumber, Node node,
			Throwable throwable) {
		super(asString("", url, lineNumber, node), throwable);
		this.url = url;
		this.lineNumber = lineNumber;
		this.node = node;
	}

	static String asString(String message, URL url, long lineNumber, Node node) {
		return message + " Url=" + url + ", lineNumber=" + lineNumber
				+ (node != null ? ", node=" + node : "");
	}

	public long getLineNumber() {
		return lineNumber;
	}

	public Node getNode() {
		return node;
	}

	public URL getUrl() {
		return url;
	}

	// @Override
	// public String toString() {
	// return super.toString() + (url != null ? ", url=" + url : "")
	// + ", lineNumber=" + lineNumber
	// + (node != null ? ", node=" + node : "") + "]";
	// }

}
