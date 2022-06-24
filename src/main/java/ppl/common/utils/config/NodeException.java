package ppl.common.utils.config;

/**
 * Signal fatal problems in getting child from container node.
 */
public class NodeException extends RuntimeException {
    public NodeException(String message) {
        super(message);
    }

    public NodeException(String message, Throwable cause) {
        super(message, cause);
    }
}
