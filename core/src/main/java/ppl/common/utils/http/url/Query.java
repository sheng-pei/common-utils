package ppl.common.utils.http.url;

import ppl.common.utils.Collections;
import ppl.common.utils.character.ascii.Mask;
import ppl.common.utils.net.URLDecoder;
import ppl.common.utils.net.URLEncoder;
import ppl.common.utils.string.Strings;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Query {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final String QUERY_DELIMITER = "&";
    private static final String NV_SEPARATOR = "=";
    private static final URLEncoder QUERY_NAME_ENCODER = URLEncoder.builder()
            .setPercentEncodingReserved(true)
            .orDontNeedToEncode(
                    Mask.asciiMask(QUERY_DELIMITER + NV_SEPARATOR)
                            .predicate().negate())
            .build();
    private static final URLEncoder QUERY_VALUE_ENCODER = URLEncoder.builder()
            .setPercentEncodingReserved(true)
            .orDontNeedToEncode(Mask.asciiMask(QUERY_DELIMITER)
                    .predicate().negate())
            .build();

    public static Query create(String name, String value) {
        return new Query(name, value, DEFAULT_CHARSET);
    }

    public static Query create(String name, String value, Charset charset) {
        return new Query(name, value, charset);
    }

    public static Query parseQuery(String query) {
        return parseQuery(query, DEFAULT_CHARSET);
    }

    public static Query parseQuery(String query, Charset charset) {
        List<Query> queries = parseQueries(query, charset);
        if (queries.size() > 1) {
            throw new IllegalArgumentException("Not single query.");
        }
        return queries.isEmpty() ? null : queries.get(0);
    }

    public static List<Query> parseQueries(String queries) {
        return parseQueries(queries, DEFAULT_CHARSET);
    }

    public static List<Query> parseQueries(String queries, Charset charset) {
        return Arrays.stream(Strings.split(queries, Pattern.quote(QUERY_DELIMITER)))
                .filter(Strings::isNotEmpty)
                .map(q -> Strings.kv(q, NV_SEPARATOR.charAt(0)))
                .map(p -> new Query(p.getFirst(), p.getSecond(), charset))
                .collect(Collectors.toList());
    }

    static String joinQuery(String query, List<Query> dynamicQueries) {
        return Stream.of(Optional.ofNullable(query)
                                .filter(Strings::isNotEmpty)
                                .orElse(null),
                        Optional.ofNullable(dynamicQueries)
                                .filter(Collections::isNotEmpty)
                                .map(qs -> qs.stream()
                                        .map(q -> q.getName() + NV_SEPARATOR + q.getValue())
                                        .collect(Collectors.joining(QUERY_DELIMITER)))
                                .orElse(null)
                ).filter(Objects::nonNull)
                .collect(Collectors.joining(QUERY_DELIMITER));
    }

    static String joinQuery(List<Query> queries) {
        return joinQuery(null, queries);
    }

    private final Charset charset;
    private final String name;
    private final String value;

    private Query(String name, String value, Charset charset) {
        this.name = QUERY_NAME_ENCODER.parse(name, charset);
        this.value = QUERY_VALUE_ENCODER.parse(value, charset);
        this.charset = charset == null ? DEFAULT_CHARSET : charset;
    }

    private Query(Query query, String value) {
        this.name = query.name;
        this.charset = query.charset;
        this.value = QUERY_VALUE_ENCODER.parse(value, query.charset);
    }

    public Query setValue(String value) {
        return new Query(this, value);
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

    public boolean nameEquals(String name) {
        return Objects.equals(this.name(), URLDecoder.decode(name, charset));
    }

    @Override
    public String toString() {
        return Strings.format("({}{}) encoded to '{}'", name, value == null ? "" : NV_SEPARATOR + value, charset);
    }

}
