package ppl.common.utils.http.url;

import ppl.common.utils.character.ascii.Mask;
import ppl.common.utils.net.URLDecoder;
import ppl.common.utils.net.URLEncoder;
import ppl.common.utils.string.Strings;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Query {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final String KEEP = "!$'()*+,;";
    private static final String QUERY_DELIMITER = "&";
    private static final String NV_SEPARATOR = "=";
    private static final URLEncoder QUERY_NAME_ENCODER = URLEncoder.builder()
            .setPercentEncodingReserved(true)
            .orDontNeedToEncode(Mask.asciiMask(KEEP).predicate())
            .build();
    private static final URLEncoder QUERY_ENCODER = URLEncoder.builder()
            .setPercentEncodingReserved(true)
            .orDontNeedToEncode(Mask.asciiMask(KEEP).predicate())
            .orDontNeedToEncode(Mask.asciiMask(NV_SEPARATOR).predicate())
            .build();

    static Query create(String name, String value) {
        return new Query(name, value, DEFAULT_CHARSET);
    }

    static Query create(String name, String value, Charset charset) {
        return new Query(name, value, charset);
    }

    static Query parseQuery(String query) {
        return parseQuery(query, DEFAULT_CHARSET);
    }

    static Query parseQuery(String query, Charset charset) {
        List<Query> queries = parseQueries(query, charset);
        if (queries.size() > 1) {
            throw new IllegalArgumentException("Not single query.");
        }
        return queries.isEmpty() ? null : queries.get(0);
    }

    static List<Query> parseQueries(String queries) {
        return Arrays.stream(Strings.split(queries, Pattern.quote(QUERY_DELIMITER)))
                .filter(Strings::isNotEmpty)
                .map(q -> Strings.kv(q, NV_SEPARATOR.charAt(0)))
                .map(p -> new Query(p.getFirst(), p.getSecond(), DEFAULT_CHARSET))
                .collect(Collectors.toList());
    }

    static List<Query> parseQueries(String queries, Charset charset) {
        return Arrays.stream(Strings.split(queries, Pattern.quote(QUERY_DELIMITER)))
                .filter(Strings::isNotEmpty)
                .map(q -> Strings.kv(q, NV_SEPARATOR.charAt(0)))
                .map(p -> new Query(p.getFirst(), p.getSecond(), charset))
                .collect(Collectors.toList());
    }

    static String queryString(List<Query> queries) {
        return Optional.ofNullable(queries)
                .map(qs -> qs.stream()
                        .map(q -> q.getName() + NV_SEPARATOR + q.getValue())
                        .collect(Collectors.joining(QUERY_DELIMITER)))
                .orElse("");
    }

    static String joinQuery(String... queries) {
        return Optional.ofNullable(queries)
                .map(qs -> String.join(QUERY_DELIMITER, qs))
                .orElse("");
    }

    static String encodeQuery(String query) {
        return encodeQuery(query, DEFAULT_CHARSET);
    }

    static String encodeQuery(String query, Charset charset) {
        return Optional.ofNullable(query)
                .map(q -> QUERY_ENCODER.parse(q, charset))
                .orElse(null);
    }

    private final Charset charset;
    private final String name;
    private final String value;

    private Query(String name, String value, Charset charset) {
        this.name = QUERY_NAME_ENCODER.parse(name, charset);
        this.value = QUERY_ENCODER.parse(value, charset);
        this.charset = charset == null ? DEFAULT_CHARSET : charset;
    }

    public Query setValue(String value) {
        return new Query(name, value, charset);
    }

    public Charset getCharset() {
        return charset;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String name() {
        return URLDecoder.decode(name, charset);
    }

    public String value() {
        return value == null ? null : URLDecoder.decode(value, charset);
    }

    public boolean nameEquals(String name) {
        return Objects.equals(this.name(), URLDecoder.decode(name, charset));
    }

    @Override
    public String toString() {
        return Strings.format("({}{}) encoded to '{}'", name, value == null ? "" : NV_SEPARATOR + value, charset);
    }

}
