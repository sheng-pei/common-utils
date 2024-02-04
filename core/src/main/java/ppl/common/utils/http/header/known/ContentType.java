package ppl.common.utils.http.header.known;

import ppl.common.utils.http.Name;
import ppl.common.utils.http.header.Context;
import ppl.common.utils.http.header.Header;
import ppl.common.utils.http.header.HeaderValue;
import ppl.common.utils.http.header.UnknownHeaderValue;
import ppl.common.utils.http.header.value.mediatype.MediaType;

@Name("Content-Type")
public class ContentType implements Header<MediaType> {

    private final HeaderValue value;

    private ContentType(String value, Context context) {
        this.value = MediaType.create(value, context);
    }

    public ContentType(MediaType mediaType) {
        this.value = mediaType;
    }

    @Override
    public HeaderValue value() {
        return value;
    }

    @Override
    public MediaType knownValue() {
        if (value instanceof UnknownHeaderValue) {
            return null;
        }
        return (MediaType) value;
    }
}
