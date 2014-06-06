package sqlrodeo;

/**
 * All exceptions thrown by this library inherit from this exception.
 */
@SuppressWarnings("serial")
public class SqlRodeoException extends RuntimeException {

    public SqlRodeoException(String message) {
	super(message);
    }

    public SqlRodeoException(String message, Throwable cause) {
	super(message, cause);
    }

    public SqlRodeoException(String message, Throwable cause,
	    boolean enableSuppression, boolean writableStackTrace) {
	super(message, cause, enableSuppression, writableStackTrace);
    }

    public SqlRodeoException(Throwable cause) {
	super(cause);
    }
}
