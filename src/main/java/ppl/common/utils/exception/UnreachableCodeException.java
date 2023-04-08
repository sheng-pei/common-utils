package ppl.common.utils.exception;

public class UnreachableCodeException extends RuntimeException {
    public UnreachableCodeException() {
        super();
    }

    public UnreachableCodeException(String message) {
        super(message);
    }

    public UnreachableCodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnreachableCodeException(Throwable cause) {
        super(cause);
    }

    protected UnreachableCodeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
