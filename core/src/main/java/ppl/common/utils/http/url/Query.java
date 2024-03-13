package ppl.common.utils.http.url;

import ppl.common.utils.net.URLDecoder;
import ppl.common.utils.pair.Pair;
import ppl.common.utils.string.Strings;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Query {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    static Query create(String name, String value) {
        return new Query(name, value);
    }

    static Query parse(String query) {
        Pair<String, String> pair = Strings.kv(query, URL.NV_SEPARATOR.charAt(0));
        return Query.create(pair.getFirst(), pair.getSecond());
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
        return value == null ? null : URLDecoder.decode(value, DEFAULT_CHARSET);
    }

    public String name(Charset charset) {
        return URLDecoder.decode(name, charset);
    }

    public String value(Charset charset) {
        return value == null ? null : URLDecoder.decode(value, charset);
    }

    @Override
    public String toString() {
        return "(" + name + (value == null ? "" : "," + value) + ")";
    }

}
