package ppl.common.utils.http.entity;

import ppl.common.utils.IOs;
import ppl.common.utils.http.Entity;
import ppl.common.utils.http.header.known.ContentType;
import ppl.common.utils.http.header.value.mediatype.MediaType;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Objects;

public class PlainTextEntity implements Entity {
    private static final ContentType DEFAULT_CONTENT_TYPE = new ContentType("text/plain");

    private final ContentType contentType;
    private final byte[] text;

    public PlainTextEntity(Charset charset, String text) {
        this(charset, text, false);
    }

    public PlainTextEntity(Charset charset, String text, boolean specificCharset) {
        Objects.requireNonNull(text);
        Objects.requireNonNull(charset);
        ContentType contentType = DEFAULT_CONTENT_TYPE;
        if (specificCharset) {
            MediaType mediaType = DEFAULT_CONTENT_TYPE.knownValue();
            mediaType.setParameter("charset", charset);
            contentType = new ContentType(mediaType);
        }
        this.contentType = contentType;
        this.text = text.getBytes(charset);
    }

    @Override
    public ContentType contentType() {
        return this.contentType;
    }

    @Override
    public void write(OutputStream os) {
        try (InputStream is = new ByteArrayInputStream(text)) {
            IOs.copy(is, os);
        } catch (Exception e) {
            throw new RuntimeException("Plain text write error.", e);
        }
    }
}
