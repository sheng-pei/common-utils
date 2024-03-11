package ppl.common.utils.http.url;

import ppl.common.utils.character.ascii.AsciiGroup;
import ppl.common.utils.character.ascii.Mask;
import ppl.common.utils.net.URLDecoder;
import ppl.common.utils.net.URLEncoder;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.function.Predicate;

public class Query {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public static Query create(String name, String value) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name is required.");
        }
        return new Query(name, value);
    }

    private final String name;
    private final String value;

    private Query(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public Query setValue(String value) {
        return new Query(name, value);
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String name() {
        return URLDecoder.decode(name, DEFAULT_CHARSET);
    }

    public String value() {
        return URLDecoder.decode(value, DEFAULT_CHARSET);
    }

    public String name(Charset charset) {
        return URLDecoder.decode(name, charset);
    }

    public String value(Charset charset) {
        return URLDecoder.decode(value, charset);
    }

    @Override
    public String toString() {
        return "(" + name + (value == null ? "" : "," + value) + ")";
    }

}
