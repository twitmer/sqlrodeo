package sqlrodeo.implementation;

import sqlrodeo.Action;

@SuppressWarnings("serial")
public class ExecutionException extends LocateableException {

    public ExecutionException(Action action, String message) {
	super(action.resolveResourceUrl(), action.resolveLineNumber(), action
		.getNode(), message);
    }

    public ExecutionException(Action action, Throwable throwable) {
	super(action.resolveResourceUrl(), action.resolveLineNumber(), action
		.getNode(), throwable);
    }
}