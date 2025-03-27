package ppl.common.utils.http.property.elements;

import ppl.common.utils.http.Client;
import ppl.common.utils.http.Name;
import ppl.common.utils.http.header.HeaderValue;
import ppl.common.utils.http.header.known.ContentType;
import ppl.common.utils.http.header.value.mediatype.MediaType;
import ppl.common.utils.http.header.value.mediatype.Mime;
import ppl.common.utils.http.property.Element;
import ppl.common.utils.http.request.Request;
import ppl.common.utils.http.request.RequestInitializer;

import java.nio.charset.Charset;

@Name(Client.CHARSET)
public class ECharset implements Element<Charset>, RequestInitializer {

    private final Charset charset;

    public ECharset(Object charset) {
        this.charset = toCharset(charset);
    }

    private Charset toCharset(Object value) {
        Charset charset;
        if (value instanceof Charset) {
            charset = (Charset) value;
        } else if (value instanceof String) {
            charset = Charset.forName((String) value);
        } else {
            throw new IllegalArgumentException(String.format(
                    "Unsupported value type: '%s' for '%s'.",
                    value.getClass(), ECharset.class.getCanonicalName()));
        }
        return charset;
    }

    @Override
    public Charset get() {
        return charset;
    }

    @Override
    public void init(Request.Builder request) {
        ContentType header = request.getHeader(ContentType.class);
        if (header != null) {
            HeaderValue value = header.value();
            if (value instanceof MediaType) {
                MediaType mediaType = (MediaType) value;
                Mime mime = mediaType.getArguments();
                String name = Client.CHARSET;
                if (mime.getByKey(name) != null && !mediaType.hasParameter(name)) {
                    mediaType = mediaType.appendLexicalParameter(name, charset.toString());
                    request.setHeader(new ContentType(mediaType));
                }
            }
        }
    }

}
