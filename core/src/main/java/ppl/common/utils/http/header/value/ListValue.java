package ppl.common.utils.http.header.value;

import ppl.common.utils.http.header.Context;
import ppl.common.utils.http.header.HeaderValue;
import ppl.common.utils.http.header.SingleLineHeaderValue;
import ppl.common.utils.http.header.value.transfercoding.Coding;
import ppl.common.utils.http.symbol.HttpCharGroup;
import ppl.common.utils.string.Strings;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class ListValue<T extends HeaderValue> extends SingleLineHeaderValue {

    public static ListValue<HeaderValue> create(String value, Context context,
                                     BiFunction<String, Character, List<String>> splitter,
                                     BiFunction<String, Context, ? extends HeaderValue> valueCreator) {
        Objects.requireNonNull(value, "Value is required.");
        Objects.requireNonNull(value, "Splitter is required.");
        Objects.requireNonNull(value, "Value creator is required.");
        List<HeaderValue> values = splitter.apply(value, DELIMITER).stream()
                .map(s -> Strings.trim(s, HttpCharGroup.WHITESPACE))
                .filter(s -> !s.isEmpty())
                .map(s -> valueCreator.apply(s, context))
                .collect(Collectors.toList());
        return new ListValue<>(value, values);
    }

    public static ListValue<Coding> create(List<Coding> values) {
        return new ListValue<>(values == null ? Collections.emptyList() : values);
    }

    private static final char DELIMITER = ',';
    private final List<HeaderValue> values;

    private ListValue(String value, List<? extends HeaderValue> values) {
        super(value);
        @SuppressWarnings("unchecked")
        List<HeaderValue> list = (List<HeaderValue>) values;
        this.values = list;
    }

    private ListValue(List<? extends HeaderValue> values) {
        @SuppressWarnings("unchecked")
        List<HeaderValue> list = (List<HeaderValue>) values;
        this.values = list;
    }

    public List<HeaderValue> getValues() {
        return values;
    }

    @Override
    public String toCanonicalString() {
        StringBuilder builder = new StringBuilder(10);
        values.forEach(v -> builder.append(v.toCanonicalString()).append(DELIMITER).append(" "));
        if (builder.length() > 0) {
            builder.setLength(builder.length() - 2);
        }
        return builder.toString();
    }
}
