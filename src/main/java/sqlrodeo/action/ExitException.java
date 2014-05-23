package sqlrodeo.action;

import sqlrodeo.SqlRodeoException;

@SuppressWarnings("serial")
public class ExitException extends SqlRodeoException {

    public ExitException() {
        super();
    }

    public ExitException(String message) {
        super(message);
    }

}
