package ppl.common.utils.security;

public class NoStrongSecureRandomException extends RuntimeException {
    public NoStrongSecureRandomException(String s) {
        super(s);
    }
}
