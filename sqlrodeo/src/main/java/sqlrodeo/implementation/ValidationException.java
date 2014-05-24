package sqlrodeo.implementation;

import java.net.URL;

import org.w3c.dom.Node;

import sqlrodeo.IAction;

@SuppressWarnings("serial")
public class ValidationException extends LocateableException {

	public ValidationException(IAction action, Throwable throwable) {
		super(action.resolveResourceUrl(), action.resolveLineNumber(), action
				.getNode(), throwable);
	}

	public ValidationException(IAction action, String message) {
		super(action.resolveResourceUrl(), action.resolveLineNumber(), action
				.getNode(), message);
	}

	public ValidationException(URL url, long lineNumber, Node node,
			String message) {
		super(url, lineNumber, node, message);
	}
}
