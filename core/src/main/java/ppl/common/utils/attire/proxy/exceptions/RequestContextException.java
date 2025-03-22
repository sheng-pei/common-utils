package ppl.common.utils.attire.proxy.exceptions;

import ppl.common.utils.attire.AttireException;

public class RequestContextException extends AttireException {
    public RequestContextException() {
    }

    public RequestContextException(String message) {
        super(message);
    }

    public RequestContextException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequestContextException(Throwable cause) {
        super(cause);
    }

    public RequestContextException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
