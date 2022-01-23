package ppl.common.utils.config;

public class NodeException extends RuntimeException {
    public NodeException() {
        super();
    }

    public NodeException(String message) {
        super(message);
    }

    public NodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public NodeException(Throwable cause) {
        super(cause);
    }
}
