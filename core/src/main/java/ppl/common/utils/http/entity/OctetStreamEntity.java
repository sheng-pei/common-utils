package ppl.common.utils.http.entity;

import ppl.common.utils.IOs;
import ppl.common.utils.http.Entity;
import ppl.common.utils.http.header.known.ContentType;
import ppl.common.utils.http.header.value.mediatype.MediaType;

import java.io.*;
import java.util.Objects;

public class OctetStreamEntity implements Entity {
    private static final ContentType DEFAULT_CONTENT_TYPE =
            new ContentType(MediaType.ensureKnown("application/octet-stream"));
    private final InputStream is;

    public OctetStreamEntity(InputStream is) {
        Objects.requireNonNull(is);
        this.is = is;
    }

    @Override
    public ContentType contentType() {
        return DEFAULT_CONTENT_TYPE;
    }

    @Override
    public void write(OutputStream os) {
        try {
            InputStream is = new BufferedInputStream(this.is);
            IOs.copy(is, os);
        } catch (Exception e) {
            throw new RuntimeException("Octet stream write error.", e);
        }
    }
}
