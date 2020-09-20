package ppl.common.utils.exception;

public class StringProcessException extends RuntimeException {

    public StringProcessException() {
        super();
    }

    public StringProcessException(String message) {
        super(message);
    }

    public StringProcessException(String message, Throwable cause) {
        super(message, cause);
    }

    public StringProcessException(Throwable cause) {
        super(cause);
    }

    protected StringProcessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
