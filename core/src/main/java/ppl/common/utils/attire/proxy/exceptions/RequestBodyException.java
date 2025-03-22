package ppl.common.utils.attire.proxy.exceptions;

import ppl.common.utils.attire.AttireException;

public class RequestBodyException extends AttireException {
    public RequestBodyException() {
        super();
    }

    public RequestBodyException(String message) {
        super(message);
    }

    public RequestBodyException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequestBodyException(Throwable cause) {
        super(cause);
    }

    public RequestBodyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
