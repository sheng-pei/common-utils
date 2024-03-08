package ppl.common.utils.config;

/**
 * Signal that data conversion fail.
 */
public class ConvertException extends RuntimeException {

    public ConvertException(String message) {
        super(message);
    }

    public ConvertException(String message, Throwable cause) {
        super(message, cause);
    }

}
