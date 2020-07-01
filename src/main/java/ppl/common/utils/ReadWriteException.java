package ppl.common.utils;

public class ReadWriteException extends RuntimeException {

    public ReadWriteException() {
        super();
    }

    public ReadWriteException(String message) {
        super(message);
    }

    public ReadWriteException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReadWriteException(Throwable cause) {
        super(cause);
    }

    protected ReadWriteException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
