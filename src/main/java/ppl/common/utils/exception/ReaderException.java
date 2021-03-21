package ppl.common.utils.exception;

public class ReaderException extends RuntimeException {
    public ReaderException() {
        super();
    }

    public ReaderException(String message) {
        super(message);
    }

    public ReaderException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReaderException(Throwable cause) {
        super(cause);
    }
}
