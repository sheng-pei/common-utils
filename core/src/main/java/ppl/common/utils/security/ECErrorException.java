package ppl.common.utils.security;

public class ECErrorException extends RuntimeException {
    public ECErrorException(String s) {
        super(s);
    }

    public ECErrorException(String s, Throwable t) {
        super(s, t);
    }
}
