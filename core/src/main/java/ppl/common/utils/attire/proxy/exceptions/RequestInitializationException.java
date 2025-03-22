package ppl.common.utils.attire.proxy.exceptions;

import ppl.common.utils.attire.AttireException;

public class RequestInitializationException extends AttireException {
    public RequestInitializationException() {
    }

    public RequestInitializationException(String message) {
        super(message);
    }

    public RequestInitializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequestInitializationException(Throwable cause) {
        super(cause);
    }

    public RequestInitializationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
