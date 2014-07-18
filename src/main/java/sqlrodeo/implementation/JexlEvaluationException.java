package sqlrodeo.implementation;

/**
 * Exception thrown when a JEXL expression or script is rejected by the JEXL
 * engine.
 */
@SuppressWarnings("serial")
public class JexlEvaluationException extends Exception {

	public JexlEvaluationException() {
	}

	public JexlEvaluationException(String message) {
		super(message);
	}

	public JexlEvaluationException(String message, Throwable cause) {
		super(message, cause);
	}

	public JexlEvaluationException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public JexlEvaluationException(Throwable cause) {
		super(cause);
	}
}
