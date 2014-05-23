package sqlrodeo;

@SuppressWarnings("serial")
public class SqlRodeoException extends RuntimeException {

    public SqlRodeoException() {
    }

    public SqlRodeoException(String message) {
        super(message);
    }

    public SqlRodeoException(Throwable cause) {
        super(cause);
    }

    public SqlRodeoException(String message, Throwable cause) {
        super(message, cause);
    }

    public SqlRodeoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
