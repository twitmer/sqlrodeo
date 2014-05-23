package sqlrodeo.implementation;

import java.net.URL;

import org.w3c.dom.Node;

@SuppressWarnings("serial")
public class ExecutionException extends LocateableException {

    public ExecutionException(URL url, long lineNumber, Node node, String message, Throwable throwable) {
        super(url, lineNumber, node, message, throwable);
    }

    public ExecutionException(URL url, long lineNumber, Node node, Throwable throwable) {
        super(url, lineNumber, node, throwable);
    }

    public ExecutionException(URL url, long lineNumber, Node node, String message) {
        super(url, lineNumber, node, message);
    }
}
