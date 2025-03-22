package ppl.common.utils.attire.proxy.server.param;

import ppl.common.utils.http.header.value.mediatype.Mime;
import ppl.common.utils.string.Strings;

public class FormPojo {
    private final Mime mime;

    public FormPojo(Form form) {
        String mime = form.mime();
        if (mime.isEmpty()) {
            mime = form.value();
        } else if (!form.value().isEmpty()) {
            throw new IllegalArgumentException("Both mime and value is specified. This is not allowed.");
        }

        Mime m = mime.isEmpty() ? null : Mime.create(mime);
        if (!Mime.MULTIPART_FORM_DATA.equals(m) && !Mime.X_WWW_FORM_URLENCODED.equals(m)) {
            throw new IllegalArgumentException(Strings.format(
                    "Form mime must be either '{}' or '{}'",
                    Mime.MULTIPART_FORM_DATA, Mime.X_WWW_FORM_URLENCODED));
        }
        this.mime = m;
    }

    public Mime mime() {
        return mime;
    }
}
