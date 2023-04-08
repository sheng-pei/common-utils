package ppl.common.utils.exception;

public class IOStreamException extends RuntimeException {

    public IOStreamException() {
        super();
    }

    public IOStreamException(String message) {
        super(message);
    }

    public IOStreamException(String message, Throwable cause) {
        super(message, cause);
    }

    public IOStreamException(Throwable cause) {
        super(cause);
    }

    protected IOStreamException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
