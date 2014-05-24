package sqlrodeo.implementation;

import sqlrodeo.IAction;

@SuppressWarnings("serial")
public class ExecutionException extends LocateableException {

	public ExecutionException(IAction action, String message) {
		super(action.resolveResourceUrl(), action.resolveLineNumber(), action
				.getNode(), message);
	}

	public ExecutionException(IAction action, Throwable throwable) {
		super(action.resolveResourceUrl(), action.resolveLineNumber(), action
				.getNode(), throwable);
	}
}
