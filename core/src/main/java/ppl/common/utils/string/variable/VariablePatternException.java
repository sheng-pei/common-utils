package ppl.common.utils.string.variable;

public class VariablePatternException extends RuntimeException {
    public VariablePatternException() {
    }

    public VariablePatternException(String message) {
        super(message);
    }

    public VariablePatternException(String message, Throwable cause) {
        super(message, cause);
    }

    public VariablePatternException(Throwable cause) {
        super(cause);
    }

    public VariablePatternException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
