package ppl.common.utils.command;

public class InvalidOptionValueException extends CommandLineException {
    public InvalidOptionValueException(String message) {
        super(message);
    }

    public InvalidOptionValueException(String message, Throwable cause) {
        super(message, cause);
    }
}
