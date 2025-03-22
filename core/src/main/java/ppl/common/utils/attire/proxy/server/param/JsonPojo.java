package ppl.common.utils.attire.proxy.server.param;

import java.nio.charset.Charset;
import java.util.Objects;

public class JsonPojo {

    private final Charset charset;

    public JsonPojo(Json json) {
        Objects.requireNonNull(json);
        this.charset = json.charset().isEmpty() ?
                null : Charset.forName(json.charset());
    }

    public Charset getCharset() {
        return this.charset;
    }

}
