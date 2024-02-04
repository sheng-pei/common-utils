package ppl.common.utils.http.header;

import ppl.common.utils.http.symbol.Lexer;

public abstract class SingleLineHeaderValue implements HeaderValue {

    protected SingleLineHeaderValue() {}

    protected SingleLineHeaderValue(String value) {
        if (!Lexer.isSingleLineFieldValue(value)) {
            throw new IllegalArgumentException("Invalid header value.");
        }
    }

}
