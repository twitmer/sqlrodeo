package sqlrodeo.implementation;

import java.net.URL;

import org.w3c.dom.Node;

import sqlrodeo.SqlRodeoException;

@SuppressWarnings("serial")
public abstract class LocateableException extends SqlRodeoException {

    private final long lineNumber;

    private final Node node;

    private final URL url;

    public LocateableException(URL url, long lineNumber, Node node, String message) {
        super(message);
        this.url = url;
        this.lineNumber = lineNumber;
        this.node = node;
    }

    public LocateableException(URL url, long lineNumber, Node node, String message, Throwable throwable) {
        super(message, throwable);
        this.url = url;
        this.lineNumber = lineNumber;
        this.node = node;
    }

    public LocateableException(URL url, long lineNumber, Node node, Throwable throwable) {
        super(throwable);
        this.url = url;
        this.lineNumber = lineNumber;
        this.node = node;
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

    @Override
    public String toString() {
        return super.toString() + (url != null ? ", url=" + url : "") + ", lineNumber=" + lineNumber
                + (node != null ? ", node=" + node : "") + "]";
    }

}
