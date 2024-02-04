package ppl.common.utils.http.response;

import ppl.common.utils.http.Headers;

import java.io.InputStream;

public abstract class Response implements Headers {

    private final ResponseCode code;

    protected Response(ResponseCode code) {
        this.code = code;
    }

    public ResponseCode getCode() {
        return this.code;
    }

    public abstract InputStream openInputStream();

}
