package ppl.common.utils.attire.proxy.exceptions;

import ppl.common.utils.attire.AttireException;

public class ResponseException extends AttireException {
    public ResponseException() {
    }

    public ResponseException(String message) {
        super(message);
    }

    public ResponseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResponseException(Throwable cause) {
        super(cause);
    }

    public ResponseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
