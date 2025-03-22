package ppl.common.utils.attire.proxy.exceptions;

import ppl.common.utils.attire.AttireException;

public class RequestParameterException extends AttireException {
    public RequestParameterException() {
        super();
    }

    public RequestParameterException(String message) {
        super(message);
    }

    public RequestParameterException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequestParameterException(Throwable cause) {
        super(cause);
    }

    public RequestParameterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
