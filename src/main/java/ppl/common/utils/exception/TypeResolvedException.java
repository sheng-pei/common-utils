package ppl.common.utils.exception;

public class TypeResolvedException extends RuntimeException {
    public TypeResolvedException() {
        super();
    }

    public TypeResolvedException(String message) {
        super(message);
    }

    public TypeResolvedException(String message, Throwable cause) {
        super(message, cause);
    }

    public TypeResolvedException(Throwable cause) {
        super(cause);
    }

    protected TypeResolvedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
