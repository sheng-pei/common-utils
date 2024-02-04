package ppl.common.utils.http.url;

import ppl.common.utils.net.URLEncoder;

import java.util.function.Predicate;

public class Query {
    private static final char DEFAULT_DELIMITER = '=';
    private static final URLEncoder DEFAULT_ENCODER = URLEncoder.builder()
            .or(Predicate.<Character>isEqual(DEFAULT_DELIMITER).negate())
            .build();

    private final char delimiter;
    private final String name;
    private final String value;
    private final URLEncoder encoder;

    private Query(String name, String value, char delimiter) {
        if (delimiter == DEFAULT_DELIMITER) {
            encoder = DEFAULT_ENCODER;
        } else {
            encoder = URLEncoder.builder()
                    .or(Predicate.<Character>isEqual(DEFAULT_DELIMITER).negate())
                    .build();
        }
        this.delimiter = delimiter;
        this.name = name;
        this.value = value;
    }

    private Query(String name, String value) {
        this(name, value, DEFAULT_DELIMITER);
    }

    public Query setValue(String value) {
        return new Query(name, value, delimiter);
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return encoder.parse(name) + (value == null ? "" : delimiter + value);
    }

    public static Query create(String name, String value) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name is required.");
        }
        return new Query(name, value);
    }

}
