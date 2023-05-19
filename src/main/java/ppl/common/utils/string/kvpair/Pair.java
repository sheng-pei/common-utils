package ppl.common.utils.string.kvpair;

import ppl.common.utils.string.Strings;

import java.util.Objects;

public class Pair {
    private static final Pair INVALID = new Pair();

    private final String key;
    private final String value;

    private Pair() {
        this.key = "";
        this.value = "";
    }

    private Pair(String key, String value) {
        if (Strings.isBlank(key)) {
            throw new IllegalArgumentException("Key is required.");
        }

        this.key = key.trim();
        this.value = value.trim();
    }

    public boolean isValid() {
        return !key.isEmpty();
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair pair = (Pair) o;
        return Objects.equals(key, pair.key) && Objects.equals(value, pair.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }

    public static Pair create(String key, String value) {
        if (Strings.isBlank(key)) {
            return INVALID;
        }
        return new Pair(key, value);
    }

}
