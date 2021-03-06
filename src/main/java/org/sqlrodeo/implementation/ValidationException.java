package org.sqlrodeo.implementation;

import java.net.URL;

import org.sqlrodeo.Action;
import org.w3c.dom.Node;

@SuppressWarnings("serial")
public class ValidationException extends LocateableException {

	public ValidationException(Action action, String message) {
		super(action.resolveResourceUrl(), action.resolveLineNumber(), action
				.getNode(), message);
	}

	public ValidationException(Action action, Throwable throwable) {
		super(action.resolveResourceUrl(), action.resolveLineNumber(), action
				.getNode(), throwable);
	}

	public ValidationException(URL url, long lineNumber, Node node,
			String message) {
		super(url, lineNumber, node, message);
	}
}
