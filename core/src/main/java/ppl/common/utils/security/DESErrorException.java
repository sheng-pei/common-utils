package ppl.common.utils.security;

public class DESErrorException extends RuntimeException {
    public DESErrorException(String s) {
        super(s);
    }

    public DESErrorException(String s, Throwable t) {
        super(s, t);
    }
}
