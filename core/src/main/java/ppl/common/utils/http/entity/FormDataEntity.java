package ppl.common.utils.http.entity;

import ppl.common.utils.http.Entity;
import ppl.common.utils.http.header.known.ContentType;

import java.io.OutputStream;

public class FormDataEntity implements Entity {

    @Override
    public ContentType contentType() {
        return null;
    }

    @Override
    public void write(OutputStream os) {

    }
}
