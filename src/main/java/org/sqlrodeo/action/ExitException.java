package org.sqlrodeo.action;

import org.sqlrodeo.SqlRodeoException;

/**
 * Exception thrown exclusively by the ExitAction to trigger the current
 * SqlRodeo processing to halt.
 */
@SuppressWarnings("serial")
public class ExitException extends SqlRodeoException {

	/**
	 * Constructor.
	 */
	public ExitException() {
		super("ExitException");
	}

}
