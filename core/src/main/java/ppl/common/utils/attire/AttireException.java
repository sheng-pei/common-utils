package ppl.common.utils.attire;

public class AttireException extends RuntimeException {
    public AttireException() {
    }

    public AttireException(String message) {
        super(message);
    }

    public AttireException(String message, Throwable cause) {
        super(message, cause);
    }

    public AttireException(Throwable cause) {
        super(cause);
    }

    public AttireException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
