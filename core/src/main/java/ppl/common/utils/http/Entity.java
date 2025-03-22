package ppl.common.utils.http;

import ppl.common.utils.http.header.known.ContentType;

import java.io.OutputStream;

public interface Entity {
    ContentType contentType();
    void write(OutputStream os);
}
