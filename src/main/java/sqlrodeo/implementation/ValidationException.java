package sqlrodeo.implementation;

import java.net.URL;

import org.w3c.dom.Node;

@SuppressWarnings("serial")
public class ValidationException extends LocateableException {

    public ValidationException(URL url, long lineNumber, Node node, String message, Throwable throwable) {
        super(url, lineNumber, node, message, throwable);
    }

    public ValidationException(URL url, long lineNumber, Node node, Throwable throwable) {
        super(url, lineNumber, node, throwable);
    }

    public ValidationException(URL url, long lineNumber, Node node, String message) {
        super(url, lineNumber, node, message);
    }
}
